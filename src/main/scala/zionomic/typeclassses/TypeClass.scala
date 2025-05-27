package zionomic.typeclassses

object TypeClass extends App {
  trait Animal:
    def makeSound(): String

  class Cat extends Animal:
    def makeSound(): String = "Meow"

  class Dog extends Animal:
    def makeSound(): String = "Woof"

  case class Labordor() extends Dog

  case class GermanShepard() extends Dog

  case class BengalCat() extends Cat

  case class BritishShortHair() extends Cat


  case class Shelter[A](var animals: List[A]) {
    def add(animal: A): Unit = animals = animal :: animals

    def size() = animals.size

    def adopt(): A = animals.head
  }

  val cat = new Cat
  val dog = new Dog

  val shelterCat = Shelter[Cat](List(cat))
  val shelterDog = Shelter[Dog](List(dog))
  //  val animal: Shelter[Animal] = shelterCat //wont work
  shelterCat.add(new Cat)
  //    shelterCat.add(new Animal{}) // wont work
  println(s"size of cat ${shelterCat.size()}")


  case class ShelterAnimal[+T]( animals: List[T]) {
//    def add[B >: T](animal: B): Unit = ???

    def size() = animals.size

    def adopt(): T = animals.head
  }

  val shelterAnimalCat = ShelterAnimal[Cat](List(cat))

  val shelterAnimal: ShelterAnimal[Animal] = shelterAnimalCat

//  val shelterANIMALDog : ShelterAnimal[Dog] = ShelterAnimal//THIS WONT WORK
  val adoptedAnimal = shelterAnimal.adopt()
  println(adoptedAnimal.makeSound())


  case class ShelterPet[-T]():
    def add(animal: T): Unit = ???
//   def adopt[A<:T](): A =??? // Error: Contravariant T in return type

  @main def run(): Unit =
    val cat = Cat()
    val dog = Dog()

    val animalShelter = ShelterPet[Animal]()
    val catShelter: ShelterPet[Cat] = animalShelter // Works with contravariance!
//    val dogShelterL : ShelterPet[Dog] = catShelter
    catShelter.add(cat) // Works: Cat <: Animal
  // catShelter.add(dog)                        // Fails: Dog not <: Cat
}

object Mian{
  // Base trait for animals
  trait Animal:
    def name: String

    def makeSound(): String

  case class Cat(name: String) extends Animal:
    def makeSound(): String = "Meow"

  case class Dog(name: String) extends Animal:
    def makeSound(): String = "Woof"

  // 1. Invariant Shelter (no variance)
  case class InvariantShelter[T](private var animals: List[T]):
    def add(animal: T): Unit = animals = animal :: animals

    def adopt(): T = animals.head // Assumes non-empty for simplicity

    def list(): List[T] = animals

  // 2. Covariant Shelter (for adopting)
  case class CovariantShelter[+T](private val animals: List[T]):
    def adopt(): T = animals.head

    def list(): List[T] = animals

  // 3. Contravariant Shelter (for adding)
  trait ContravariantShelter[-T]:
    def add(animal: T): Unit

  // Concrete implementation of ContravariantShelter
  class AnimalContravariantShelter extends ContravariantShelter[Animal]:
    private var animals: List[Animal] = List()

    def add(animal: Animal): Unit = animals = animal :: animals

    def list(): List[Animal] = animals // For debugging

  // Main program to test variance
   def run(): Unit =
    val cat = Cat("Whiskers")
    val dog = Dog("Rex")

    // Invariant Shelter
    println("=== Invariant Shelter ===")
    val invariantCatShelter = InvariantShelter[Cat](List(cat))
    invariantCatShelter.add(cat) // Works: Cat into Cat shelter
    // invariantCatShelter.add(dog)           // Fails: Dog isn’t Cat
    println(s"Adopted: ${invariantCatShelter.adopt().makeSound()}") // "Meow"
    // val invariantAnimalShelter: InvariantShelter[Animal] = invariantCatShelter  // Fails

    // Covariant Shelter
    println("\n=== Covariant Shelter ===")
    val covariantCatShelter = CovariantShelter[Cat](List(cat))
    val covariantAnimalShelter: CovariantShelter[Animal] = covariantCatShelter // Works!
    println(s"Adopted as Animal: ${covariantAnimalShelter.adopt().makeSound()}") // "Meow"
    // val covariantDogShelter: CovariantShelter[Dog] = covariantAnimalShelter  // Fails

    // Contravariant Shelter
    println("\n=== Contravariant Shelter ===")
    val animalContraShelter: ContravariantShelter[Animal] = AnimalContravariantShelter()
    val catContraShelter: ContravariantShelter[Cat] = animalContraShelter // Works!
    catContraShelter.add(cat) // Works: Cat <: Animal
    // catContraShelter.add(dog)  // Fails: Dog not <: Cat
    println(s"Shelter contents: ${animalContraShelter.asInstanceOf[AnimalContravariantShelter].list().map(_.makeSound())}") // "[Meow]"
}
