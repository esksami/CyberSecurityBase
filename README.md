# Application description

Project assignment for Cyber Security Base 2019-2020.

A very vulnerable task management app where each registered user can create and edit their tasks. There are also features for editing your password and filtering these tasks by their name.

# Project Report

## A1 - Injection

### description
When a user views their tasks they have the option to filter these tasks by name. 

This filtering feature is vulnerable to SQL injections. In other words any knowledgeable user could write raw SQL into the filter and have it executed by the application.

Because the filter string is formatted into a simple LIKE operator an attacker can exploit this by injecting their own LIKE operator that would be true for each row in the database table. Here, the filter `%' OR '' LIKE '%` would result in an SQL command like 
```sql
SELECT t FROM Task t WHERE t.username = 'hello'AND t.name LIKE '%%' OR '' LIKE '%%'
```
which is always true because of the injected OR and LIKE operators together cause the `WHERE` clause to be always true.

The attacker will gain read access to all tasks in the database, regardless of their owner.

### how to fix

A common approach to preventing SQL injections is using parameterized queries, so instead of just formatting and appending the filter to the SQL query directily, the filter string along with the query string would be given to a function specifically made for creating SQL queries safely.

Another option would be using a query builder but the approach is still the same.

In this case it actually took a lot more effort to have an SQL injection in the application than simply making a safe query with the `@Query` annotation which is too restrictive to allow you to have SQL injections in your query.

The fix is just creating the query in a parameterized way with the `@Query` annotation instead of jumping through additional hoops:
```java
    @Query("SELECT task FROM #{#entityName} task WHERE task.username = :username" + 
           "AND task.name  LIKE CONCAT('%',:filter,'%')")
    List<Task> filterByName(
        @Param("username") String username,
        @Param("filter") String filter
    );
```


## A2 - Broken Authentication:

### description
The application allows users to have well known or weak passwords and it doesn't support multi-factor authentication. On top of this, an attacker can try an unlimited number of passwords as fast as the server is able to process the requests.

It is also not possible to recover your account because there is no email or any other verification.

These vulnerabilities allow the attacker to use an automated attack that attempts to login with known common passwords or just brute force guessing by trying any combinations of characters.

This means that the attacker would gain full access to other users accounts (along with the information in those accounts) if they were using common or otherwise short/weak passwords.

### how to fix
The application should require users to have sufficiently complex passwords to prevent brute force and password guessing attacks.

One way to prevent the attacker from gaining access is to lock the account after multiple failed logins but this would allow denial of service attacks where the attacker locks victims accounts on purpose. It is also ineffective against slow attacks and trying a single password against a large list of usernames.

Optionally all login requests from the same source could be throttled, meaning only a limited number of requests would be allowed during some time period. This would still be ineffective against slow attacks but also attacks that are distributed throughout many IPs. 

The previous two solutions also aren't very good against credential stuffing where the attacker has a list of valid usernames and passwords. Luckily, their shortcomings can be combatted against by requiring multi-factor authentication from all users.


## A3 - Sensitive Data Exposure

### description
Passwords are stored as plaintext and they are shown when a user enters their profile.

Although there isn't a way to gain direct access to the user records in the database an attacker could get the password of a particular user by using an XSS vulnerability as follows:

```javascript
<script>
fetch('http://localhost:8080/profile')
  .then((response) => {
    return response.text();
  }).then((text) => {
    console.log(text)
    // send credentials shown on the profile page to a place of your choosing
  })
</script>
```

### how to fix
This can be fixed by using a strong hash algorithm in combination with salting. The hashing transforms the password into an unrecognizeable string (a hash) that is very difficult to reverse back to its original form. The random salt is concatenated with the original password before hashing so that no two passwords result in the same hash.

Without salting, an attacker with access to the database records can use pre-calculated hashes to figure out the passwords.

After this, both the hash and the salt are then stored in the database so that we are able to check if a given password attempt is correct (by transforming that password attempt through the hash).

To fix all this here, we can use bcrypt to as the password encoder instead of the useless `NoOpPasswordEncoder`:
```java
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
```
and also not show a users password in their profile.


## A5 - Broken Access Control

### description

The task editing page can be accessed by anyone who knows the id of the task they want to edit. It is not required to be logged in or the owner of the task. Also, the task ids are very predictable integers.

The attacker can go to the path `/tasks/edit/{id}` attempting all ids starting from 1 and edit the tasks with XSS or CSRF.

This allows the attacker to edit XSS and CSRF into the names of the tasks without the victim even noticing it. We already know what can be done with this from the 'Sensitive Data Exposure'-section.

- Use unpredictable ids for tasks
- Make sure the user editing a task is the owner of the task.
- Use authentication.

### how to fix
One thing we could do is use long enough random strings as the task id to make them practically impossible to guess.

The main issue here though is the lack of access control. Accessing a tasks edit page should require that the user is authenticated AND the owner of the task. We need to add the 'tasks/edit' path to the antMatchers function in the SecurityConfiguration:
```java
    http.authorizeRequests().antMatchers("/tasks/edit", ... ).authenticated();
```
and check that the current user owns that task
```java
    if (!task.getUsername().equals(authentication.getName())) {
        return "redirect:/tasks";
    }
```


## A6 - Security Misconfiguration: CSRF

### description

The task names are vulnerable to CSRF. If the attacker has access write access to other users tasks, they can insert html in to the task names such as
```html
<body onload="document.csrf.submit()">
  <form action="http://localhost:8080/password/edit" method="POST" name="csrf" style="display: none;">
    <input type="text" name="password" value="hacked"/>
  </form>
</body>
```
which will change the password of the victim to 'hacked'.

### how to fix

This is an intentional misconfiguration that can be undone by removing the line
```java
http.csrf().disable();
```
from `SecurityConfiguration.java` and including the CSRF token with
```html
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
```
in our forms.

Now all CSRF attacks will fail because they can't include the CSRF token.

## A7 - Cross-Site Scripting (XSS)

### description

The task names are also vulnerable to Cross-Site Scripting, which means that malicious code can be inserted into them. The simplest way to test this is to set a task name as the following:
```javascript
<script> alert("Hello World!"); </script>
```
But it can be used along with the 'Broken Access Control' and 'Sensitive Data Exposure' vulnerabilities to steal user passwords as already shown.

### how to fix

To prevent this we can use `th:text` instead of `th:utext` for setting the task names. To do this we can replace
```html
<span th:utext="${task.name}">task name</span>
```
in `tasks.html`, with the following:
```html
<span th:text="${task.name}">task name</span>
```
and the inserted html will no longer be interpreted as part of the page.
