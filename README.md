# Author's thoughts
Yes I am aware that a lot of code in this repo breaks good design approach, OOP etc. However when I implemented it I was not aware of it :) Now the impementation was postponed and I treat this project as a battlefield of trying new technologies.

# Architecture overview
![alt tag](https://github.com/MateuszPu/gpw_radar/blob/dev/architecture.png)

# Building and running it on localhost
What should you do to build and run application?
First of all install [Docker] and [Docker-compose]. After that run following command (assuming you are in root of project)

    docker build -t build:1.0 .
    docker run -d --name build build:1.0 && docker cp build:configs/app target

After that you should se new folder target and then run following command:

    docker-compose up -d

Go to localhost:8080 and you should see working application.
# Issue/task tracker
Under following link (https://trello.com/b/P8ASjAks/gpw-radar) I manage my work on the project.

[JHipster]: https://jhipster.github.io/
[Docker]: https://docs.docker.com/install/
[Docker-compose]: https://docs.docker.com/compose/install/
