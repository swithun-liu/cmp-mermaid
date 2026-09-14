export const puppeteerLaunchOptions = {
  headless: 'shell',
  ...(process.env.CI === 'true'
    ? {
        args: ['--no-sandbox', '--disable-setuid-sandbox'],
      }
    : {}),
};
