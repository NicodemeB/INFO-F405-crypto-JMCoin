#echo "runing update"
BRANCH=$1
DIR='/home/crypto/'
cd $DIR'JMCoin'

git stash >> /dev/null
git fetch >> /dev/null


CURRENTCOMMITNUMBER=$(git rev-list --count origin/$BRANCH)
LOCALCOMMITNUMBER=$(cat $DIR'commits.txt')

#echo "curent" $CURRENTCOMMITNUMBER
#echo "local" $LOCALCOMMITNUMBER

BLUE='\033[1;34m'
NC='\033[0m' # No Color

if [ "$((LOCALCOMMITNUMBER))" -lt "$((CURRENTCOMMITNUMBER))" ]
then
	echo "" > $DIR'jm.log';
	echo "${BLUE}[$(date) - SCRIPT UPDATE INFO] Seems to have a new commit..${NC}"
	echo $((CURRENTCOMMITNUMBER)) > $DIR'commits.txt'

	rm -rf $DIR'JMCoin'
	cd $DIR
	git clone https://github.com/NicodemeB/JMCoin.git --branch=$BRANCH
	cd $DIR'JMCoin'

	echo "${BLUE}[$(date) - SCRIPT UPDATE INFO] editting localhost->master.jmcoin.technology${NC}"
	NETCONST=$DIR'JMCoin/src/main/java/com/jmcoin/network/NetConst.java'
	# rpl "localhost" "master.jmcoin.technology" /home/crypto/JMCoin/src/main/java/com/jmcoin/network/NetConst.java
	sed -i 's/localhost/master.jmcoin.technology/g' /home/crypto/JMCoin/src/main/java/com/jmcoin/network/NetConst.java

	echo "${BLUE}[$(date) - SCRIPT UPDATE INFO] edited ${NC}"

	mvn package

	if [ $(ps -ax |grep 'RunRelay' | awk '{print $1}' |grep -c "") -gt 0 ]
        then
		echo "${BLUE}[$(date) - SCRIPT UPDATE INFO] Killing old java ...${NC}"
		sudo kill $(ps -ax |grep 'RunRelay' | awk '{print $1}')
	fi

	echo "${BLUE}[$(date) - SCRIPT UPDATE INFO] Running new commit ...${NC}"
	sleep 15
	mvn exec:java -Dexec.mainClass="com.jmcoin.runme.RunRelay" -Dexec.args="master.jmcoin.technology"
#else
#	echo "${BLUE}[SCRIPT UPDATE INFO] Seems to NOT have a new commit..${NC}"
fi