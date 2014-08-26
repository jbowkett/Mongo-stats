
db.regions¡.aggregate([
{ $unwind : "$populations" },
{ $match : {"populations.year":2012} },
{ $sort  : {"populations.growth":-1} },
{ $limit : 2}
]);