### Intro
- We need to apply parallel programming concepts to maximise the use of cpu cores (hardware). We can achieve this using Parallel Streams API.
- We need to apply asynchronous programming (non-blocking for efficient resource utilisation) to ensure there are no blocking i/o calls in microservices architecture as this impacts the latency of the application. We can achieve this using CompletableFuture API.
- The common things among Parallel Streams and CompletableFuture API is using threads to improve performance of code, both of these programming styles uses functional style of programming.
- Parallel Streams and CompletableFuture were introduced in Java 8 along with Lambdas and Streams API.
    - FYI, Flow API (Pub-Sub way of programming) was introduced in Java 9, Flow API is official support for reactive streams specification since Java 9.

### Concurrency vs Parallelism

#### Concurrency
- Concurrency is a concept where 2 or more tasks can run simultaneously.
- In java, concurrency is achieved by threads.
    - Are the tasks running in interleaved fashion?
    - Are the tasks running simultaneously?
- It depends on underlying core where we are trying to run this task?
- If we are running task on a single core machine then it means tasks are running in an interleaved fashion because there is only one core, CPU has a scheduler which takes care of scheduling multiple threads to run on a single core in an interleaved fashion.
- If we are running multiple task in multi-core machine then it means tasks are literally running simultaneously.
- Normally threads interact with each other using a shared object (root of all evil) as we can run into race condition, deadlock issues, to deal with this we have to use synchronised statements/methods, semaphores, etc.

#### Parallelism
- 2 or more tasks are literally going to run in parallel.
- Steps:
    - Decompose tasks into multiple sub-tasks (fork).
        - Size of sub-task should be such that it cannot be broken down further.
    - Executing these sub-tasks in sequential.
        - This means all sub-tasks would now be executed on different multiple cores of the machine.
    - Joining results of these tasks (join).
- This process is also called fork/join.

### Thread API
- Threads API got introduced in Java1.
- Threads are basically used to offload the blocking tasks as background tasks.
- Threads allowed the developers to write asynchronous style of code.
```
thread1.start();
thread2.start();
thread1.join();
thread2.join();
```
- Using join() is not recommended because join will block the calling thread (drawback of this approach).
- For example, if you run the program from the main method then it will block the main thread until the thread whose Join method is called has completed.
- Use this method to ensure that a thread has been terminated. The caller will block indefinitely if the thread does not terminate.
- More here: https://learn.microsoft.com/en-us/dotnet/api/system.threading.thread.join?view=net-7.0
- Misc: https://phuctm97.com/blog/sync-async-concurrent-parallel

#### Limitation of this API
- Create the thread manually.
- Start the thread manually.
- Join the thread manually.
- Threads are expensive, threads have their own runtime-stack, memory, registers and more.
- This is primarily the reason that creating and destroying thread is an expensive operation.
    - To solve this problem, ThreadPool was created.

### ThreadPool
- Group of threads that are created and readily available to handle any work submitted to them.
- Benefits?
    - We don't need to manually create, start and join threads.

#### Executor service
- Async task execution engine in Java.
- Executor service = Thread Pool + Work Queue (where tasks are placed) + Completion Queue (where completed tasks are placed)

### Future API
- When we get a hold of the Future and then calling get() on it will block the thread thats executing the code. In this case it's the main thread.
- Using Thread is not good because, its very verbose and you have the call the start and join function to get the data from them
- Future is an advancement to Thread, but still its not good because there is no good way to combine data from multiple futures.
- CompletableFuture solves all the limitations that from the Thread and FutureAPI.
    - It is pretty easy to create an asynchronous task using CompletableFuture 
    - It is pretty easy to combine data from multiple future by creating the reactive pipeline using the different thenXXX() functions.

- ExecutorService is designed to achieve task based parallelism.
- Fork/Join framework is designed to achieve data parallelism.

### Parallel Stream API
- When we use parallel streams, number of tasks that can run in parallel = number of cores in machine.
- We can use parallel streams to process a collection's items in parallel by default it is done sequentially i.e. one by one. Syntax comparison below:


```
return list
.stream()
.map(String::toUpperCase)
.collect(Collectors.toList())


return list
.parallelStream()
.map(String::toUpperCase)
.collect(Collectors.toList()) 
```

#### Data parallelism
- Task is recursively split into sub-tasks until it reaches its least possible size and execute these tasks in parallel.
- Basically, Fork/Join framework uses divide and conquer approach.

#### Fork/Join framework
- Client submit ForkJoin task (different from regular task) to ForkJoin Pool.
- Work Queue + Worker Threads, result would be returned to client.

### parallelStream() - How it works?
- Split
    - Data is split into small data chunks.
    - Example: List collection split into chunks of elements of size 1.

- Execute
    - Data chunks are applied to the stream pipeline and the operations are executed using ForkJoinPool (to execute them parallely).

- Combine
    - Combine the executed results into a final result.
    - Use collect() or reduce() functions for same.

#### Spliterator

Spliterator in parallel streams:
- Data source is split into multiple chunks by the spliterator.
- Each and every collection has a different spliterator implementation.
- Performance differ based on the implementation.
- Since ArrayList is an indexed collection so using parallel streams (which uses spliterator) vs sequential stream (default) was giving better results since it is easy to split.
- Using parallel streams for LinkedList gives disappointing results, this is because this type of collection is difficult to split into individual chunks.

Important Note:
- Always always test your code before you assume that parallel stream is going to give better performance.
- Invoking parallelStream() does not guarantee faster performance of your code. 
- This is because parallel stream needs to perform additional steps compared to sequential which are splitting, executing and combining.    
    - If collection can be easily split (like ArrayList) then it would improve performance of code and if not (like LinkedList) then it would reduce performance of the code.
- Recommendation: Always compare the performance before you use parallelStream().

Misc:
- When boxing (primitive to object) and unboxing (object to primitive) are involved then it leads to poor performance if parallel streams is used compared to streams (sequential).
- We should always compare performance of our code with and without parallel streams so as to evaluate our decision.

#### Final Computation Result Order
- The final computation result order for parallel stream depends upon the type of collection and spliterator implementation of the collection.
- If the collection is ordered like an ArrayList then order is maintained.
- If the collection is unordered like Set then order is NOT maintained.
- reduce() function is used to reduce the computation into a single value. It always works on data pairs.
```
Example usage:
Sum -> reduce(0, (x,y) -> x+y)
Multiply -> reduce(1, (x,y) -> x*y)
```
- collect() performs the combine phase in a mutable fashion whereas reduce() produces a immutable results and has a greater memory footprint.

### CompletableFuture
- It is an async reactive functional programming API.
- It is created to solve the limitations of Future API.

#### More about Reactive API
- Responsive:
    - Fundamentally asynchronous.
    - Call will return immediately and response is sent to caller code when it's available.

- Resilient:
    - Exception or error won't crash the app or code.

- Elastic:
    - Async computations normally run in a pool of threads.
    - Number of threads can go up or down based on need.

- Message driven:
    - Async computations interact with each through messages in an event driven style.

#### CompletableFuture can be grouped under 3 categories

- Factory methods: To initiate async computations.
- Completion Stage methods: Chain async computations.
- Exception methods: Handle exceptions in an async computation.

Lets discuss these 2 methods of CompletableFuture:

- supplyAsync()
    - It is factory method to initiate async computation.
    - Input is supplier functional interface.
    - Returns CompletableFuture<T>()

- thenAccept()
    - CompletionStage method
    - Chain async computation
    - Input is consumer functional interface.
    - Consumes result of the previous computation.
    - Returns CompletableFuture<Void>()
    - Normally the last step in async computation
    - Normally used to log something and move on.
    
- .thenCompose(Function<R, CompletableStage<?>>) 
    - It is used to invoke another async function.
    - We can use output of previous completable future as input of the completable future returned in thenCompose.

**Note**
- In client side, we have to use join() method to retrieve the result.
- In server side, we would just return the CompletableFuture (no blocking of thread at all), the client can call .join() method at their end to retrieve the result, so we aren't blocking calling thread at server side.

### Exception Handling for CompletableFuture

CompletableFuture has 3 options to handle exceptions:
- handle() - Catch exception and recover. Takes BiFunction(input: result and exception, return value: recovery value) as input, we can access both result of previous execution in CF pipeline and the exception. Invoked always as part of CF pipeline
- exceptionally() - Catch exception and recover. Takes Function(input: exception, return value: recovery value) an input. Invoked only if exception is thrown as part of CF pipeline. Recommended as we don't have to write any logic for success path.
- whenComplete() - Catch exception and does not recover. Takes BiConsumer (input: result and exception). No recovery value.

**Note**
- We can put .exceptionally to handle exceptions wherever we anticipate.
- After an exception is thrown, all operations in CF pipeline would be skipped until handled by any handle operation like exceptionally and post that execution of below operations in CF pipeline would be continued.

### Completable Future - Default ThreadPool

- By default, CompletableFuture uses the Common ForkJoinPool.
- The number of threads in the pool = number of cores in the machine.
- This Common ForkJoinPool is shared by:
    - Parallel Streams
    - CompletableFuture
- Its common for application to use ParallelStreams and CF together
- The following issues may occur:
    - Thread being blocked by a time consuming task.
    - Thread not available.
- We can create user-defined thread pool and use that instead of ForkJoinPool.
