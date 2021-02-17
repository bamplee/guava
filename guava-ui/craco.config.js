const CracoLessPlugin = require('craco-less');

module.exports = {
    plugins: [
        {
            plugin: CracoLessPlugin,
            options: {
                lessLoaderOptions: {
                    lessOptions: {
                        modifyVars: {'brand-primary': '#00802E'},
                        javascriptEnabled: true,
                    },
                },
            },
        },
    ],
};