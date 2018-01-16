const $colors = [
    '#ffffff',
    '#fbf305',
    '#fd6361',
    '#dd0907',
    '#f20884',
    '#4700a5',
    '#0000d3',
    '#02abea',
    '#1fb714',
    '#006412',
    '#562c05',
    '#a6593d',
    '#daa989',
    '#c98a5e',
    '#404040',
    '#000000'
];

const vm = new Vue({
    el: '#app',

    data: {
        mode: null,
        scale: null,
        context: null,
        colors: $colors,
        selectedColor: $colors[0],
        ws: null,
        recInterval: null,
        nextAt: null
    },

    computed: {
    },

    methods: {
        handleResize: function () {
            const clientHeight = document.documentElement.clientHeight;
            const clientWidth = document.documentElement.clientWidth;

            if (clientWidth > clientHeight * 1.2) {
                this.mode = 'landscape'
            } else {
                this.mode = 'portrait'
            }

            this.scale = Math.round(Math.min(clientHeight, clientWidth) / 120);
        },

        selectColor: function (color) {
            this.selectedColor = color
        },

        draw: function (x, y, c) {
            const rgb = $colors[c];

            const imageData = this.context.createImageData(1, 1);
            imageData.data[0] = parseInt(rgb.slice(1, 3), 16);
            imageData.data[1] = parseInt(rgb.slice(3, 5), 16);
            imageData.data[2] = parseInt(rgb.slice(5, 7), 16);
            imageData.data[3] = 255;

            this.context.putImageData(imageData, x, y)
        },

        handleClick: function (event) {
            const now = new Date().getTime();

            if (this.nextAt && this.nextAt > now) {
                //alert('You have to wait at least ' + Math.round((this.nextAt - now) / 1000) + " seconds before next pixel can be put");
                //return;
            }

            const rect = this.$refs.canvas.getBoundingClientRect();
            const self = this;

            $.ajax({
                url: 'pixel',
                type: 'PUT',
                data: {
                    x: Math.floor((event.clientX - rect.left) / this.scale),
                    y: Math.floor((event.clientY - rect.top) / this.scale),
                    c: $colors.indexOf(this.selectedColor)
                },
                success: function (response) {
                    self.nextAt = response + 5 * 1000;
                },
                error: function (response, status) {
                    alert('Pixel placement ended with error: ' + response + ", status: " + status);
                }
            });
        },

        connect: function () {
            const self = this;

            if (this.ws === null) {
                this.ws = new SockJS("/place/sockjs");

                this.ws.onopen = function () {
                    console.log("WebSocket connection opened");
                };

                this.ws.onmessage = function (message) {
                    switch (message.data[0]) {
                        case 'B':
                            if (message.data[1] === '2') {
                                for (i = 2; i < message.data.length;) {
                                    const a = parseInt(message.data[i++], 16);
                                    const b = parseInt(message.data[i++], 16);
                                    const c = parseInt(message.data[i++], 16);
                                    const d = parseInt(message.data[i++], 16);

                                    self.draw((a << 2) | (b >>> 2), (b & 0x03) | (c & 0x0F), d);
                                }
                            } else if (message.data[1] === '3') {
                                for (i = 2; i < message.data.length;) {
                                    const a = parseInt(message.data[i++], 16);
                                    const b = parseInt(message.data[i++], 16);
                                    const c = parseInt(message.data[i++], 16);
                                    const d = parseInt(message.data[i++], 16);
                                    const e = parseInt(message.data[i++], 16);
                                    const f = parseInt(message.data[i++], 16);

                                    self.draw((a << 6) | (b << 2) | (c >>> 2), ((c & 0x03) << 8) | (d << 4) | (e & 0x0F), f);
                                }
                            } else {
                                console.log("Unsupported pixel size: " + message.data[1])
                            }
                            break;
                        case 'S':
                            for (i = 1, x = 0, y = 0; i < message.data.length; i++) {
                                const c = parseInt(message.data[i], 16);

                                self.draw(x, y, c);

                                if (++x === 100) {
                                    x = 0;
                                    ++y;
                                }
                            }
                    }
                };

                this.ws.onclose = function () {
                    console.log("WebSocket connection closed");

                    self.ws = null;

                    clearInterval(this.recInterval);
                    self.recInterval = setInterval(self.connect.bind(self), 1000);
                };
            }
        }
    },

    created: function () {
        window.addEventListener('resize', this.handleResize);

        this.handleResize();
    },

    mounted: function () {
        this.context = this.$refs.canvas.getContext('2d');

        this.connect();
    }
});
