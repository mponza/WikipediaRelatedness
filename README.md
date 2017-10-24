![WikipediaRelatedness](http://pages.di.unipi.it/ponza/public/images/wikipediarelatedness/logo.png)



Introduction
=============
Welcome to the Wikipedia Relatedness libary. This repository stores all algorithms that have been implemented in the paper `A Two-Stage Framework for Computing Entity Relatedness in Wikipedia`. More documentation and some code cleaning will be done in the next months.


Relatedness Webservice API
==========================

We provide the possiblity to use the algorithms present in the Wikipedia Relatedness library as a Rest-like interface. This API can be automatically queried via Json data.


Relatedness between Two Wikipedia Entities
------------------------------------------

**Endpoint**: `http://wikirel_url/rel`

**Description**. Given two entities `srcWikiID` and `dstWikiID`, it computes the relatedness between them (several methods are available).

**Request** (mandatory parameters: `srcWikiID`, `dstWikiID` and `method`):

    {
        "srcWikiID": Int - Wikipedia ID of the Wikipedia entity src
        "dstWikiID": Int - Wikipedia ID of the Wikipedia entity dst
        "method": String - Relatedness method. It can be:
                           "jaccard"
                           "milnewitten"
                           "2stage-mw"
                           "2stage-mwdw"
    }

**Response**:

    {
        "srcWikiID": Int - Wikipedia ID of the Wikipedia entity srcWikiID
        "dstWikiID": Int - Wikipedia ID of the Wikipedia entity dstWikiID
    
        "relatedness": Float -  Strength of the relatedness between srcWikiID and dstWikiID
    }
    
    
Ranked Entities for Entity Expansion
------------------------------------------

**Endpoint**: `http://wikirel_url/rank`

**Description**: Given an entity `srcWikiID`, it returns a list of the most related entities to `srcWikiID` (several methods are available).

**Request** (mandatory parameters: `srcWikiID`and `method`):

    {
        "srcWikiID": Int - Wikipedia ID of the Wikipedia entity src
        "srcWikiTitle": String - Wikipedia Title of the Wikipedia entity src
    
        "method": String - Relatedness used for ranking/expansion of src. It can be:
                           "jaccard"
                           "milnewitten"
                           "2stage-mw"
                           "2stage-mwdw"
                           "esa"
    }

**Response**:

    {
        "srcWikiID": Int (see above)
        "method": String (see above)
        "rankedEntities":
            [
                {
                    "dstWikiID": Int = Wikipedia ID of an entity related to srcWikiID
                    "relatedness": Float - Strength of the relatedness between srcWikiID and dstWikiID
                }
            ]
    }
    

ESA Text-Entity Expansion
-------------------------

**Endpoint**: `http://wikirel_url/text`

**Description**: Given a short input text, it returns a list of most related entities by using Explicit Semantic Analysis.

**Request**:

    {
        "text": String - Text to be expanded with Wikipedia Entities
    }

**Response** (very similar to Ranked Entities for Query Expansion‘s Response):

    {
        "text": String (as above)
        "rankedEntities":
            [
                "dstWikiID"
                "dstWikiTitle"
                "relatedness"
            ]
    }


Getting `wikirel_url`
---------------------

For obtaining an already-working endpoint (`wikirel_url`) just send an email to the [first author](http://pages.di.unipi.it/ponza/contact/) of the paper. We are planning to deploy the whole relatedness webservice into the [SoBiData](http://www.sobigdata.eu/) infrastructure... stay tuned! 



Citation and Further Reading
==========================

If you find any resource (code or data) of this repository useful, please cite our paper:

> [Marco Ponza](http://pages.di.unipi.it/ponza), [Paolo Ferragina](http://pages.di.unipi.it/ferragina/), [Soumen Chakrabarti](https://www.cse.iitb.ac.in/~soumen/):
> A Two-Stage Framework for Computing Entity Relatedness in Wikipedia.
> *In Proceedings of 26th International Conference on Information & Knowledge Management (CIKM 2017)*.


License
=======
The code in this repository has been released under Apache License 2.0.
