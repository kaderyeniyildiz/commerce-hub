# mvn
mvn clean install -DskipTests

# postgresql
brew services start postgresql@19
brew services list
psql postgres
brew services stop postgresql@19

# mvn
mvn clean package -DskipTests
