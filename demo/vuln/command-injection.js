const cp = require('child_process');
function run(userInput) {
  // BAD: command injection
  cp.exec('cat ' + userInput);
}
run(process.argv[2]);
