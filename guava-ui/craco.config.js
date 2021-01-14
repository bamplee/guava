const CracoLessPlugin = require('craco-less');

module.exports = {
    plugins: [
        {
            plugin: CracoLessPlugin,
            options: {
                lessLoaderOptions: {
                    lessOptions: {
                        modifyVars: {'brand-primary': '#2E92FC'},
                        javascriptEnabled: true,
                    },
                },
            },
        },
    ],
};