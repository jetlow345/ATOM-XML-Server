java AggregationServer 4567 &
AggregationServer=$!
sleep 2

java ContentServer 4567 input1.txt &
sleep 2

java GetClient 4567 &
sleep 2

kill $AggregationServer

