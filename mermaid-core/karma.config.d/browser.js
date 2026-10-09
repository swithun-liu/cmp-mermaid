config.set({
  browsers: ['ChromeHeadlessCmpMermaid'],
  customLaunchers: {
    ChromeHeadlessCmpMermaid: {
      base: 'ChromeHeadless',
      flags: [
        '--no-sandbox',
        '--disable-setuid-sandbox',
        '--remote-debugging-port=0',
      ],
    },
  },
  captureTimeout: 180000,
  browserDisconnectTimeout: 180000,
  browserNoActivityTimeout: 180000,
});
