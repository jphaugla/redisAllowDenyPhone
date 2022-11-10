echo 5000
curl -X GET -H "Content-Type: application/json" http://localhost:5000/checkCache
echo "\n"
echo 5001
curl -X GET -H "Content-Type: application/json" http://localhost:5001/checkCache
