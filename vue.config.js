module.exports = {
    runtimeCompiler: true,

    css: {
        sourceMap: true
    },

    lintOnSave: false,

    chainWebpack: config => {
        config
            .plugin('html')
            .tap(args => {
                args[0].template = "src/index.html"

                return args
            });
        config
            .plugins.delete('copy')
        // config
        //     .plugin('copy')
        //     .tap(args => {
        //         args = [
        //             [{
        //                 from: '/src',
        //                 to: '/public/asset/favicon.ico',
        //                 toType: 'file',
        //             }]
        //         ]
        //         return args
        //     });
    },

    outputDir: 'public',
    assetsDir: 'assets'
}
