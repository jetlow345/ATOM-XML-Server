echo "Basic Put Get Test"
./TestOneGetPut.sh > result/PutGetTest.txt

if diff -q result/PutGetTest.txt expected/expect1.txt &>/dev/null;
then
    echo "Test Failed"
else
    echo "Test Pass"
fi

echo "Mutliple Content Server Test"
./MultipleContentServer.sh > result/MultipleContentServerTest.txt 

if diff -q result/MultipleContentServerTest.txt expected/expect4.txt &>/dev/null;
then
    echo "Test Failed"
else
    echo "Test Pass"
fi

echo "Persistance and Replication of AggregationServer"
./TestReplication_N_Persistance.sh > result/Replication_N_PersistanceTest.txt

if diff -q result/Replication_N_PersistanceTest.txt expected/expect5.txt &>/dev/null;
then
    echo "Test Failed"
else
    echo "Test Pass"
fi

echo "Server Crash Client Test"
./TestServerCrashClient.sh > result/ServerCrashClientTest.txt 

if diff -q result/ServerCrashClientTest.txt expected/expect2.txt &>/dev/null; 
then
    echo "Test Failed"
else
    echo "Test Pass"
fi

echo "Server Crash Content Server Test"
./TestServerCrashContent.sh > result/ServerCrashContentServerTest.txt

if diff -q result/ServerCrashContentServerTest.txt expected/expect3.txt &>/dev/null;
then
    echo "Test Failed"
else
    echo "Test Pass"
fi