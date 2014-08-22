

// read in each region
// for each year, calculate the difference to the previous year
// save on each population entry in a field called growth

//then print the results of the following query
db.regions_3.aggregate([
    { $unwind : "$populations" },
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
    {
      $match : {  $or: [{year:2011}, {year:2012}]    }
    },
    { $sort  : {"growth":1} },
    { $limit : 2}
])

