const port = 9090;

const http = require('http'),
    server = http.createServer().listen(port);
const fs = require('fs');


server.on('request', (req, res) => {
    var body = [];
    console.log('request received');

    req.on('data', function(chunk) {
        body.push(chunk);
    }).on('end', function() {
        body = Buffer.concat(body).toString();
        console.log('header : ' + JSON.stringify(req.headers));
        console.log('body : ' + body);
        switch (req.url) {
            case '/firstRequest':
                res.writeHead(200, {
                    'Content-Type': 'application/xml'
                });
                fs.createReadStream('Response.xml').pipe(res);
                break;
            case '/secondRequest':
                res.writeHead(200, {
                    'Content-Type': 'application/xml'
                });
                fs.createReadStream('Response2.xml').pipe(res);
                break;
            default:
                res.writeHead(404, {
                    'Content-Type': 'text/plain'
                });
                res.end('Error 404: resource not found.');
        }


        // at this point, `body` has the entire request body stored in it as a string
    });

});

console.log('Server Running on Port : ' + port);
