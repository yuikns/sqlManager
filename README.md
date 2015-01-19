sqlManager
==========

code for orgnization rank function

# usage

scr.api.Usage.java
1. build index(usually for update)
buildLuceneIndex()
2. export to mongo
exportToMongo()



# overview
## api
warp for function in scr.core
scr.api.Usage.java

## core
api function for index-building, query, evaluate, etc. 

###scr.core.BuildAllMessIndex.java
readSql()// build lucene index
	1.index directory:
	./indexDirSql
	2.index field:
	id,author,jconf,year,firstAuthorOrg,secordAuthorOrg,thirdAuthorOrg,nCite,content
	3.notice:
	year-> range from 2003-2013(now)
	jconf-> abbr. of 39 CCF A class conferences in uppercase
	XXAuthorOrg-> raw organization data extracted from paper or website
	nCite-> -1 means not found
	content-> all the things above 

###scr.core.OrgMetaData.java


## model
provide some class defined for paper, org, etc

## service
provide basic service function
