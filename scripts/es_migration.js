const fs = require('fs');
const http = require('http')

function prepareRequest(path) {
    const options = {
        hostname: 'localhost',
        port: 9200,
        path: path,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    }

    const req = http.request(options);
    req.on('error', err => {
        console.error(err);
    });

    return req;
}

function loadBookData(filename, path) {
    const book = fs.readFileSync(filename, {encoding: 'utf-8'});
    const content = book.split('\n');
    for (let i = 0; i < content.length; i++) {
        content[i] = content[i].trim();
    }

    const title = path.toLowerCase().replace('_', ' ');

    return {
        path: path,
        title: title,
        content: content
    };
}

const rawBooks = fs.readdirSync('./books');
const parsedBooks = [];
rawBooks.forEach(file => {
    if (file.endsWith('.txt')) {
        const title = file.replace('.txt', '');
        parsedBooks.push(loadBookData(`./books/${file}`, title));
    }
});

console.log('Sending data to elasticsearch...');

parsedBooks.forEach(book => {
    book.content.forEach((line, i) => {
        const req = prepareRequest(`/book/${book.path}/${i}`);
        const entry = {
            title: book.title,
            lineNumber: i,
            content: line
        }
        req.write(JSON.stringify(entry));
        req.end();
    });
});

console.log('Done!');