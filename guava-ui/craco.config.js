const CracoLessPlugin = require('craco-less');

module.exports = {
    style: {
        postcss: {
            plugins: [
                require('tailwindcss'),
                require('autoprefixer'),
            ],
        },
    },
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