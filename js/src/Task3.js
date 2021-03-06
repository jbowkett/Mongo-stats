// task 3
// persist this for each region

db.regions_3.aggregate([
    { $unwind : "$populations" },
    { $match : {"populations.year": {$gt : 2008} } },
    {
      $project : {
        _id : 1,
        year : "$populations.year",
        growth : "$populations.growth"
      }
    },
    {
        $group :{
          _id : "$_id",
          avg_growth : { $avg: "$growth" }
        }
    }
])


// run this query:
db.regions_3.aggregate([
    { $unwind : "$populations" },
    { $match : {"populations.year" : {$gt:2008} }  },
    {
      $project : {
        _id : {
          country : "$country",
          region : "$region"
        },
        year : "$populations.year",
        avg_growth : "$avg_growth",
        annual_growth : "$populations.growth",
        deviation : { $subtract : ["$avg_growth", "$populations.growth"] }
      }
    },
    {
      $project : {
        _id : "$_id",
        year : "$year",
        annual_growth : "$annual_growth",
        avg_growth : "$avg_growth",
        absolute_deviation : {
           //had to use internet for this bit
           $cond: [
             { $lt: ['$deviation', 0] },
             { $subtract: [0, '$deviation'] },
             '$deviation'
           ]
        }
      }
    },
    { $sort:{_id :1, absolute_deviation:-1}  }
])

//then print the top 2 for each region


