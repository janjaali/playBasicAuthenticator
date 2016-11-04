# playBasicAuthenticator
Simple BasicAuthentication for Play-Framework-Apps (implemented as Filter).

# usage

1. Add as dependecy to your project:

    ```scala
    libraryDependencies ++= Seq(
        "net.habashi" %% "basicauthenticator" % "1.0.0"
    )
    ```
    
    or

    ```scala
    lazy val myProject = project.dependsOn(basicauthenticator) 
    ```

2. Extend HttpFilters and configure username/password for the BasicAuthenticator:

    ```scala
    class Filters @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends HttpFilters {

      val username = "sonic the hedgehog"
      val password = "rocks"

      override def filters = Seq(new BasicAuthenticationFilter(username, password))
    }
    ```

3. Add the extended HttpFilters as Filter to the <code>application.conf</code>

    ```bash
    # Filters
    play.http.filters=net.habashi.filters.Filters
    ```
