const crypto = require('crypto');
crypto.createHash('md5').update('password').digest('hex'); // BAD: weak hash
