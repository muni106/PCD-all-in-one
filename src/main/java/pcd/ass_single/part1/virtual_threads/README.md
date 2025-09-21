Virtual Threads solution — directory documentation

Overview
This package contains a minimal example of solving the “count PDFs that contain a word” problem using Java virtual threads (Project Loom).
Given a directory path D and a word P, it launches a virtual thread per file to check whether the file’s extracted text contains P. A monitor coordinates workers and provides the final count.

Contents
- ExtractText.java — The entry point. Orchestrates virtual-thread workers over the files in the given directory, starts an output thread, and prints the number of files that contain the given word.
- Monitor.java — A simple monitor built with ReentrantLock and Condition. Tracks how many files matched and signals when all workers have finished.

How it works
1) Input handling
   - The main expects exactly two arguments: <directory> <word>.
   - It lists the files directly under the provided directory (not recursive). If none are found, it prints an error.

2) Concurrency model with virtual threads
   - A Monitor is initialized with the number of files to process.
   - An output virtual thread calls monitor.get(), which blocks until all files have been processed, then prints the final count.
   - The program creates a virtual-thread-per-task executor (Executors.newVirtualThreadPerTaskExecutor()). For each file index i it submits a task that, in turn, starts a named virtual thread (virtualThread[i]) to process that file.
     Note: In this implementation, each executor task immediately spawns another virtual thread to do the real work. Functionally this is fine (virtual threads are very cheap), though you could simplify by submitting the processing logic directly to the executor without the extra Thread.ofVirtual().start(...).

3) File processing and PDF text extraction
   - containsWord(File pdf, String word) loads the file with PDFBox (PDDocument.load(...)). If content extraction is permitted, it uses PDFTextStripper to obtain the document text and checks String.contains(word).
   - The search is:
     - Case-sensitive
     - A simple substring match (no tokenization, no regex)
   - On success, the worker calls monitor.foundWord(true). On any IOException or if the word is not found, it calls monitor.foundWord(false).

4) Completion and result
   - Monitor.foundWord(found) increments the internal count when found is true and decrements the remaining-files counter. When all files have reported, it signals the condition.
   - Monitor.get() blocks until all files have reported and then returns the final count to the output thread.

Prerequisites
- JDK 21 or later. This code uses Thread.ofVirtual and the virtual-thread-per-task executor, both standardized in Java 21.
- Maven 3.9+ (to build), already configured in the project’s pom.xml.
- PDFBox 2.0.x is declared as a dependency in the root pom.xml and is required at runtime.

Build
From the project root:
- mvn -q -DskipTests package
This compiles the code and places class files under target/classes.

Run
You can run the main class in different ways:
1) From your IDE (recommended)
   - Open the project, ensure Project SDK is Java 21.
   - Right-click ExtractText.main and Run.
   - Program arguments: <directory> <word>

2) With the Maven Exec plugin (without adding it to the POM)
   - mvn -q org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=pcd.ass_single.part1.virtual_threads.ExtractText -Dexec.args="<directory> <word>"

3) Using java with a built classpath (Linux/macOS example)
   - First build: mvn -q -DskipTests package
   - Then run:
     java -cp "target/classes:$(mvn -q -DincludeScope=runtime -DskipTests org.apache.maven.plugins:maven-dependency-plugin:3.6.1:build-classpath -Dsilent | tail -n 1)" \
       pcd.ass_single.part1.virtual_threads.ExtractText <directory> <word>
   - On Windows, replace ':' with ';' in the -cp argument and remove the command substitution.

Expected output
- The program prints a line like:
  OOOOOOOOOOO: N
  where N is the number of files whose extracted text contains the provided word.

Notes and limitations
- Non-recursive: Only files in the top-level of the provided directory are processed; subdirectories are ignored.
- File types: All files returned by File.listFiles() are attempted with PDFBox. Non-PDF files will generally cause an IOException and are treated as “no match”.
- Case-sensitive: The search uses String.contains(word), which is case-sensitive and substring-based.
- Resource management: PDDocument is closed in both the success and non-success paths; errors lead to a false result.
- Concurrency detail: The code both uses an executor and manually starts virtual threads per file inside each submitted task. You can simplify by submitting the per-file logic directly to the executor.

Possible extensions
- Make directory traversal recursive.
- Filter only *.pdf files before processing.
- Add case-insensitive and/or regex-based searches.
- Count occurrences instead of boolean found/not-found.
- Stream partial progress (e.g., how many files processed so far) via the output thread or periodic logging.
- Robust CLI (usage help, error messages, validation).