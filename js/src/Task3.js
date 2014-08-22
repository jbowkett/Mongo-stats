// task 3
// persist this for each region
db.regions_3.aggregate([
    { $unwind : "$populations" },
    {
      $project : {
        _id : {
          country : "$country",
          region : "$region"
        },
        year : "$populations.year",
        growth : "$populations.growth"
      }
    },
    {
        $group :{
          _id : "$_id"
        },
        avg_growth : { $avg: "$growth" }
    }
])

// do this query:
db.regions_3.aggregate([
    { $unwind : "$populations" },
    {
      $project : {
        _id : {
          country : "$country",
          region : "$region"
        },
        year : "$populations.year",
        growth : "$populations.growth",
        deviation : { $subtract : ["$avg_growth", "$populations.growth"] }
      }
    },
    {
      $project : {
        _id : {
          country : "$country",
          region : "$region"
        },
        year : "$populations.year",
        growth : "$populations.growth",
        absolute_deviation : {
           //had to use internet for this bit
           $cond: [
             { $lt: ['$deviation', 0] },
             { $subtract: [0, '$deviation'] },
             '$deviation'
           ]
        }
      }
    }
])

//then print the top 2 for each region


