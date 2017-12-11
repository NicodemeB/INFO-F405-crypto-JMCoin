#echo "runing update"
BRANCH=$1
DIR='/home/crypto/'

cd $DIR'JMCoin'

git stash >> /dev/null
git fetch >> /dev/null



CURRENTCOMMITNUMBER=$(git rev-list --count origin/$BRANCH)
LOCALCOMMITNUMBER=$(cat $DIR'commits.txt')

# echo "curent" $CURRENTCOMMITNUMBER
# echo "local" $LOCALCOMMITNUMBER

BLUE='\033[1;34m'
NC='\033[0m' # No Color

if [ "$((LOCALCOMMITNUMBER))" -lt "$((CURRENTCOMMITNUMBER))" ]
then
	echo "${BLUE}[$(date) - SCRIPT UPDATE INFO] Seems to have a new commit..${NC}"
	echo $((CURRENTCOMMITNUMBER)) > $DIR'commits.txt'

	rm -rf $DIR'JMCoin'
	cd $DIR
	git clone https://github.com/NicodemeB/JMCoin.git --branch=$BRANCH
	echo "${BLUE}[$(date) - SCRIPT UPDATE INFO] Editing persistence.xml${NC}"
	cp $DIR'persistence.xml' $DIR'JMCoin/src/main/resources/META-INF/persistence.xml'

	cd $DIR'JMCoin'
	
	mvn package

	if [ $(ps -ax |grep 'TestMasterNode' | awk '{print $1}' |grep -c "") -gt 0 ]
        then
		echo "${BLUE}[$(date) - SCRIPT UPDATE INFO] Killing old java ...${NC}"
		sudo kill $(ps -ax |grep 'TestMasterNode' | awk '{print $1}')
	fi

	

	echo "${BLUE}[$(date) - SCRIPT UPDATE INFO] Running new commit ...${NC}"
	mvn exec:java -Dexec.mainClass="com.jmcoin.test.TestMasterNode"
#else
#	echo "${BLUE}[SCRIPT UPDATE INFO] Seems to NOT have a new commit..${NC}"
fi