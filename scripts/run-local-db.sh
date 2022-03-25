docker run -d \
    -e MONGO_INITDB_ROOT_USERNAME=admin \
    -e MONGO_INITDB_ROOT_PASSWORD=pass \
    -e MONGO_INITDB_DATABASE=test-db \
    -p 27017:27017 \
    mongo
