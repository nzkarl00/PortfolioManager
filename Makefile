help:
	@echo "Repository Make Commands"
	@echo "	clean-local-data: Remove local database and stored user content"

clean-local-data:
	@echo "clean-local-data: Remove local database and stored user content"
	rm ./portfolio/database/data.mv.db
	rm ./identityprovider/database/data.mv.db
	rm -rf ./identityprovider/user-content/**
