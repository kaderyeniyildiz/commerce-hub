# mvn
mvn clean install -DskipTests

# postgresql
brew services start postgresql@18
brew services list
psql postgres
brew services stop postgresql@18
