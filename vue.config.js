module.exports = {
    assetsDir: 'assets',
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
    },
}
