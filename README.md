notes-server is a RESTful note server.

# Usage

## Deploy using docker

### Build Docker image

To build the docker image run:

```
docker build -t note-server:latest .
```

### Usage

To run it:

```
docker run -d -p 8080:8080 -v ./data:/app/data note-server
```

## Deploy native

To compile and run:

```
./gradlew run
```


# Open issues

- Authenticated users
- Secured access over https (with a reverse proxy)

