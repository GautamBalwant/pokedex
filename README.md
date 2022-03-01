# Pokedox Application

# how to run

Software needed to run
 . Java 8
 . Maven
 
Steps to run
 . Open a terminal or command prompt at project root folder
 . Run " mvn clean install" command to build the project
 . Run following command "java -jar target/pokedex-0.0.1-SNAPSHOT.jar" to start the application 
  
# about APIs

As per the challenge two apis are exposed at port 8080
 - /pokemon/{name}
 - /pokemon/translated/{name}

(e.g http://localhost:8080/pokemon/mewtwo )

 # get pokemon by name API
  - This Api takes pokemon name as a path variable
  - First call the "https://pokeapi.co/api/v2/pokemon/{name} api
  - Retrieve the species URI and then make call to species api to get the description, habitat and lengendary status of the pokemon
  - The result is stored in the in-memory cache using name as key
  - In case of exception the cache for the pokemon is cleared
  - Note some pokemon observed that the habitat was null (e.g "gible") , in those cases "not found" is returned as habitat 
  
 # Translate pokemon description API
  - This Api takes pokemon name as a path variable
  - First check if the details are present in cache , else make the api calls to fetch the pokemon details
  - Then the based on the habitat and status of the pokemon the uri for translation is reterived (eg Yoda or Shakespeare)
  - Call to relevant translation API is made to get the translation
  - In case of exception during the translation API call the default description is returned  
  - The result is stored in the in-memory cache using name as key
  
  
# how to access
    . http://localhost:8080/pokemon/gible
	 output : 
	    {
			"name": "gible",
			"description": "It nests in small, horizontal holes in cave walls. 	It pounces to catch prey that stray too close.",
			"habitat": "not found",
			"lengendary": false
		}
	. http://localhost:8080/pokemon/translated/gloom
	output :
		{
			"name": "gloom",
			"description": "What appears to be drool is actually sweet honey. It is very sticky and clings stubbornly if touched.",
			"habitat": "grassland",
			"lengendary": false
		}
	

# Assumption

 - It is assumed that the description for the pokemon will not be null
 - Also description is strored in an array, In this implementation only the first element array value is used for the description
 - Note some pokemon observed that the habitat was null (e.g "gible") , in those cases "not found" is returned as habitat 
 - As using the cache it is assumed that he Pokemon details will not be changed in the public apis 
 
 
# production API changes

 - Using in-memory cache but in Production need to use a seperate cache system (eg redis) which will also support the distributed env
 - Have not used docker , in production recommended to create docker image
 - Have not added actuator endpoint to check api health in Production it is recomended
 
 
# Pokemon tested
	pikachu
	ekans
	raichu
	clefairy
	golbat
	gloom
	paras
	mewtwo
	mew
	eevee
	gible
