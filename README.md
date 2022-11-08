
### Getting started
1. Maven Build All Services
    ```
    $ mvn clean verify
    ```

2. Docker Build Images & Run Container
    ```
    $ docker-compose up -d --build
    ```

3. Update Code and Rerun
   ```
    $ mvn -pl [service-name], [other-services] -am clean install
    $ docker-compose up -d --build [service-name]
   ```