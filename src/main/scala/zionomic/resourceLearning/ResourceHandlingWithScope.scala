package zionomic.resourceLearning
import zio._
import java.io.{File, FileReader, FileWriter}

// Define the resource acquisition and release logic
object FileResources {
  // Resource 1: FileReader for reading from a file
  def fileReader(name: String): ZIO[Scope, Throwable, FileReader] =
    ZIO.acquireRelease(
      acquire = ZIO.attempt {
        val file = new File(name)
        println(s"Opening FileReader for $name")
        new FileReader(file)
      }
    )(release = reader =>
      ZIO.attempt {
        reader.close()
        println(s"Closed FileReader for $name")
      }.orDie // Ignore errors in release
    )

  // Resource 2: FileWriter for writing to a file
  def fileWriter(name: String): ZIO[Scope, Throwable, FileWriter] =
    ZIO.acquireRelease(
      acquire = ZIO.attempt {
        val file = new File(name)
        println(s"Opening FileWriter for $name")
        new FileWriter(file)
      }
    )(release = writer =>
      ZIO.attempt {
        writer.close()
        println(s"Closed FileWriter for $name")
      }.orDie // Ignore errors in release
    )
}

// Main application
object ResourceHandlingWithScope extends ZIOAppDefault {
  // Function to process the files (read and write)
  def processFiles(reader: FileReader, writer: FileWriter): ZIO[Any, Throwable, Unit] = for {
    buffer <- ZIO.attempt(new Array[Char](1024))           // Buffer to read into
    bytesRead <- ZIO.attempt(reader.read(buffer))          // Read from input file
    text = new String(buffer, 0, bytesRead).toUpperCase    // Convert to uppercase
    _ <- ZIO.attempt(writer.write(text))                   // Write to output file
    _ <- Console.printLine(s"Processed: $text")            // Log the result
  } yield ()

  // Program: Acquire both files and process them
  val program =  {
    // Compose the two resources sequentially using zip
    val files: ZIO[Scope, Throwable, (FileReader, FileWriter)] =
      FileResources.fileReader("src/main/scala/zionomic/resourceLearning/TestingFile2.txt")
        .zip(FileResources.fileWriter("src/main/scala/zionomic/resourceLearning/TestingFile.txt"))

    // Use the resources
    files.flatMap { case (reader, writer) =>
      processFiles(reader, writer)
    }
  }

  // Run the program
  override def run: ZIO[Any, Throwable, Unit] =
    program
      .provideLayer(Scope.default)
      .catchAll(error => Console.printLine(s"Error occurred: $error"))
}

// Create an input file for testing (optional, run separately)
/*object Setup extends ZIOAppDefault {
  def run = ZIO.attempt {
    val writer = new FileWriter("input.txt")
    writer.write("Hello, ZIO!")
    writer.close()
    println("Setup complete: input.txt created")
  }.orDie
}*/