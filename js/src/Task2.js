

// read in each region
// for each year, calculate the difference to the previous year
// save on each population entry in a field called growth

//then print the results of the following query
db.regions_3.aggregate([
    { $unwind : "$populations" },
    { $match : {"populations.year":2012} },
    {
      $project : {
        _id : {
          country : "$country",
          region : "$region"
        },
        year : "$populations.year",
        population : "$populations.population",
        growth : "$populations.growth"
      }
    },
    { $sort  : {"growth":-1} },
    { $limit : 2}
])

{       $project : {         _id : {           country : "$country",           region : "$region"         },         year : "$populations.year",         population : "$populations.population",         growth : "$populations.growth"       }     },

db.regions_3.aggregate([
{ $unwind : "$populations" },
{ $match : {"populations.year":2012} },
{ $sort  : {"populations.growth":-1} },
{ $limit : 2}
]);