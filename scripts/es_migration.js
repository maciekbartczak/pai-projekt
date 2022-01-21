const fs = require('fs');
const elasticsearch = require('@elastic/elasticsearch')


function loadBookData(filename, path) {
    const book = fs.readFileSync(filename, {encoding: 'utf-8'});
    let content = book.split('\n');
    for (let i = 0; i < content.length; i++) {
        content[i] = content[i].trim().replace(new RegExp('_', 'g'), '');
        if (content[i] === '') {
            content.splice(i, 1);
        }
    }

    let title = path.toLowerCase().replace(new RegExp('_', 'g'), ' ');
    title = title.charAt(0).toUpperCase() + title.slice(1);
    console.log(title);

    return {
        title: title,
        content: content
    };
}

const rawBooks = fs.readdirSync('./books');
const parsedBooks = [];
rawBooks.forEach(file => {
    if (file.endsWith('.txt')) {
        const fileName = file.replace('.txt', '');
        parsedBooks.push(loadBookData(`./books/${file}`, fileName));
    }
});

console.log(`Prepared ${parsedBooks.length} books with ${parsedBooks.map(book => book.content.length).reduce((prev, current) => prev + current)} lines`);
console.log('Sending data to elasticsearch...');

const client = new elasticsearch.Client( {
    node: "http://localhost:9200/"
});

parsedBooks.forEach(book => {
    book.content.forEach(async (line, i) => {
        const document = {
            index: "book",
            type: "book",
            body: {
                title: book.title,
                lineNumber: i,
                content: line
            }
        };
        client.index(document, (error, _) => {
            if (error) {
                console.log(error);
            }
        });
    });
});
