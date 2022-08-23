help:
	@echo "Helper scripts"
	@echo "	publish-shared: Publish content from the ./shared directory appropriately."
	@echo "	clean-local-data: Remove local database and stored user content"
	@echo "	show-coverage: Generate coverage reports and provide URLs to open in browser."
	@echo "	pre-merge: Run tests and lints, do this before attempting a merge."
	@echo "	kill-servers: Kill any running IDP or Portfolio servers."
	@echo "	test-selenium: Spin up Portfolio and IDP and run selenium tests."

clean-local-data:
	@echo "clean-local-data: Remove local database and stored user content"
	rm ./portfolio/database/data.mv.db
	rm ./identityprovider/database/data.mv.db
	rm -rf ./identityprovider/user-content/**

test-portfolio:
	@echo "Portfolio: running Gradle Test"
	cd ./portfolio; ./gradlew test

lint-portfolio:
	@echo "Portfolio: running Checkstyle"
	cd ./portfolio; ./gradlew check

test-idp:
	@echo "Identity Provider: running Gradle Test"
	cd ./identityprovider; ./gradlew test

lint-idp:
	@echo "Identity Provider: running Checkstyle"
	cd ./identityprovider; ./gradlew check



PID := $(shell ps aux | grep -E '[g]radlew -classpath .*portfolio' | tr -s ' ' | head -n 1 | cut -d ' ' -f2)
IID := $(shell ps aux | grep -E '[g]radlew -classpath .*identityprovider' | tr -s ' ' | head -n 1 | cut -d ' ' -f2)

kill-servers:
	@echo "Killing portfolio and IDP servers if they exist"
	if [ -n "$(PID)" ]; then kill "$(PID)"; fi
	if [ -n "$(IID)" ]; then kill "$(IID)"; fi

test-selenium:
	@echo "test-selenium: Running selenium tests"
	./run-selenium.sh


pre-merge: test-portfolio test-idp lint-portfolio lint-idp
	@echo "pre-merge: Runs tests and lints, to ensure working branch is ready to be merged"

show-coverage: test-portfolio test-idp
	@echo ""
	@echo ""
	@echo "Open the following file URLs in your browser to view coverage."
	@echo "file://`realpath ./portfolio/build/jacoco/html/index.html`" 
	@echo "file://`realpath ./identityprovider/build/jacoco/html/index.html`" 

publish-shared:
	@echo "Publish content from the shared/ directory to maven local"
	cd ./shared; ./gradlew clean
	cd ./shared; ./gradlew publishToMavenLocal
