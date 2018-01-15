
**Spring Boot, Active Directory, OAuth2, Spring Security, Angular JS, React**
1. Integrate Spring Security OAuth2 and Azure Active Directory, due the timeout issues in official
   Spring Boot Azure Active Directory starter, you can find more details here - https://github.com/spring-projects/spring-security/issues/4474#issuecomment-356987137
2. Check how it works with Microsoft example based on Angular JS
3. Try to verify how it works with React

**How to run the sample**
* If you want to check how it works with Angular JS, do not forget to update clientId and clientSecret
  in _application.yml_ and _app.js_. 
  After that you can build it with `clean build` gradle command and run with `spring-boot:run` command from the gradle.
* If you want to check how it works with React, you need to follow all steps described in _/react/README.MD_, keep in mind
  that you will have to replace Angular JS artifacts in _/src/main/resources/static_ folder.
  After that you can build it with `clean build` gradle command and run with `spring-boot:run` command from the gradle.
