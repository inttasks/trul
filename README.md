# How to run
```
To start server :  ./start.sh
If the file does not permission to run: chmod +x start.sh
To stop: docker-compose down 
```
Start uses docker and docker-compose, but it uses multi-stage docker and builds java project inside stage one.
Because of this reason it can take sometime to download and build everything for the first time.

After server starts you can run specified REST APIs in the task.

Note on Dockerfile: for both stages it uses same base image to reduce number of images it downloads but for production it is better to use a lightweight image for running the server in second stage.

#Design Decisions
Based on project's description we kinda want to build a system that enriches data in multiple stages.
So ideally I would have liked to implement a pipeline or chain of responsibility architecture. 
But here because of the scope, current implementation only partially does that and each step calls the next step in a more hard coded way but also in a reactive manner.
Current approach uses injection so there is some possibility of reconfiguration and flexibility in implementation and extension, but still has distance from robust and flexible data enrichment pipeline.
The current implementation still allows choosing between different strategies and components to enrich the data.

Because of IO heavy nature of this project specially from external third party service providers, reactive programming was chosen for better resource utilization.

For switching between Yoda and Shakespeare translations, because there is not that much complexity to handle the different requirements, just a simple strategy pattern is implemented.
At first, I had implemented with more abstraction with a common interface and using some factory pattern and dependency injection so each class can decide if it needs to do operation or not, but ultimately decided against it as the only difference between two Yoda and Shakespeare is an url path,
which can be implemented by simple function that decides which strategy needs to be chosen. For more complex scenario it is better to avoid this approach as this will create a complex and nested conditional flows.
As stated the better approach is each strategy decides if it needs to be applied or not, or have some sort of hashing mechanism that can access required strategy without going into complex and nested conditions.

#Folder Structure
```
.
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── inttasks
│   │   │           └── trul
│   │   │               ├── common
│   │   │               ├── pokemon
│   │   │               │   ├── client
│   │   │               │   ├── controller
│   │   │               │   ├── dto
│   │   │               │   ├── model
│   │   │               │   └── service
│   │   │               └── translator
│   │   │                   ├── client
│   │   │                   ├── dto
│   │   │                   ├── enums
│   │   │                   └── service

```

There are 3 main folders. First one is **common** which is usually needed to share common things with other sub-sections of the project, and files and directories inside it don't make sense to have their packaging and structure outside. 
Here we don't have much common things between different part of code that we can reuse.
Next is **pokemon** folder which contains different for Pokemon module that handles incoming request via REST APIs.
The Next main folder is **translator** which contains code for handling translation. 
The reason this being a separate folder is that you may want to have different implementation and different usage later on for a different module or service.

I think one of the most important things in structuring code is to follow same convention everywhere. So the subfolder in each of these modules or packages represent same thing for that package and module:
- controller: if package need controller, those classes go there
- client: contains code for third parties client and APIs. You can extract some clients to common package or have separate package for clients that are used by more than one package. 
- dto: dto objects reside there
- model: any model/dao object goes here. Depend on the project you may not to have an object that serves both as model and dto.
- service: contains services that handle service layer logic. Services are usually connect different part of code to each other, so it is a good place to understand the code.
- enums: contains enum types

The main reason for this structuring is to encapsulate and isolate different parts of code from each other based on their responsibilities and have some separation in order to make finding related things easier.
This way you probably won't have a folder with many classes under it, and if code still goes that way it would be better to organize classes with more related domain in a lower level sub-folders.

#Production API Improvements
- An API versioning is recommended. So future changes can be integrated easier.
- The GET /pokemon/translated/:name is better to be changed to /pokemon/:name/translate
- Caching is probably needed as the logic is heavily reliant on third parties with limited rate and performance.
- If the enrichment process is done in multiple stages, and some later stages are failing, we might want to add circuit breakers to reduce costs and side effects. For example only allow few queries to go to third parties to first reduce our cost of consuming healthy ones and also putting less pressure on non-healthy ones.
- Also, failure cases should be discussed. So if one stage fails what will happen to the result. Should an incomplete result to be returned, or we should return an error, and how will that error look like?

#Tests
There are two unit test files for each of Services that contains multiple tests for each of them, covering various failure and success scenarios.
There are also few simple integrated tests that starts from controller with mocked third parties API. Adding not mocked API test are also actually very easy in this case but decided against it so the tests be self-contained.
It won't be a bad idea to have some contract tests as well. Overall 14 tests have been written.










