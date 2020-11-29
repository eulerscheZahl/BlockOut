import * as THREE from './build/three.module.js';
import * as orbit from './jsm/controls/OrbitControls.js';

export class module3 {
    cubes = {}
    pool = []
    scene = new THREE.Scene();
    renderer = new THREE.WebGLRenderer();

    static get name() {
        return 'board'
    }

    reinitScene(container, canvasData) {}

    updateScene(previousData, currentData, progress) {
        if (this.container.style.width != this.container.parentElement.clientWidth + "px") {
            const width = this.container.parentElement.clientWidth;
            const height = width * 9 / 16;
            this.container.style.width = width;
            this.container.style.height = height;
            this.container.children[0].style.width = width;
            this.container.children[0].style.height = height;

            this.renderer.setSize(width, height);
            this.camera.aspect = width / height;
            this.camera.updateProjectionMatrix();
        }

        var start = 0;
        if (currentData) start = currentData.number;
        const end = start + 1;
        const t = start + progress;
        for (var id in this.cubes) {
            const c = this.cubes[id];
            if (t < c.states[0].t) c.material.opacity = 0;
            else {
                var idx = 0;
                while (c.states.length > idx + 1 && c.states[idx + 1].t < t) idx++;
                var before = c.states[idx];
                var after = c.states[idx];
                var frac = 0
                if (idx + 1 < c.states.length) {
                    after = c.states[idx + 1];
                    frac = (t - before.t) / (after.t - before.t);
                }
                c.material.color.set(before.color);
                c.material.opacity = before.opacity + frac * (after.opacity - before.opacity);
                c.position.x = before.x + frac * (after.x - before.x);
                c.position.y = before.y + frac * (after.y - before.y);
                c.position.z = before.z + frac * (after.z - before.z);
            }
        }
    }

    handleFrameData(frameInfo, data) {
        if (!data) return
        const color = Math.floor(Math.random() * (1 << 24));
        data.split(';').forEach(s => {
            const d = s.split(' ')
            if (d[0] == 'P') { // pit dimensions
                const width = +d[1];
                const height = +d[2];
                const depth = +d[3];

                var material = new THREE.LineBasicMaterial({
                    color: 0x888888,
                    opacity: 0.2
                });

                var gridGeo = new THREE.Geometry();

                for (var x = 0; x <= width; x++) {
                    gridGeo.vertices.push(new THREE.Vector3(x - 0.5, -0.5, -0.5));
                    gridGeo.vertices.push(new THREE.Vector3(x - 0.5, -0.5, depth - 0.5));
                    gridGeo.vertices.push(new THREE.Vector3(x - 0.5, -0.5, -0.5));
                    gridGeo.vertices.push(new THREE.Vector3(x - 0.5, height - 0.5, -0.5));
                    gridGeo.vertices.push(new THREE.Vector3(x - 0.5, -0.5, depth - 0.5));
                    gridGeo.vertices.push(new THREE.Vector3(x - 0.5, height - 0.5, depth - 0.5));
                }
                for (var z = 0; z <= depth; z++) {
                    gridGeo.vertices.push(new THREE.Vector3(-0.5, -0.5, z - 0.5));
                    gridGeo.vertices.push(new THREE.Vector3(width - 0.5, -0.5, z - 0.5));
                    gridGeo.vertices.push(new THREE.Vector3(-0.5, -0.5, z - 0.5));
                    gridGeo.vertices.push(new THREE.Vector3(-0.5, height - 0.5, z - 0.5));
                    gridGeo.vertices.push(new THREE.Vector3(width - 0.5, -0.5, z - 0.5));
                    gridGeo.vertices.push(new THREE.Vector3(width - 0.5, height - 0.5, z - 0.5));
                }
                for (var y = 1; y <= height; y++) {
                    gridGeo.vertices.push(new THREE.Vector3(-0.5, y - 0.5, -0.5));
                    gridGeo.vertices.push(new THREE.Vector3(width - 0.5, y - 0.5, -0.5));
                    gridGeo.vertices.push(new THREE.Vector3(-0.5, y - 0.5, depth - 0.5));
                    gridGeo.vertices.push(new THREE.Vector3(width - 0.5, y - 0.5, depth - 0.5));
                    gridGeo.vertices.push(new THREE.Vector3(-0.5, y - 0.5, -0.5));
                    gridGeo.vertices.push(new THREE.Vector3(-0.5, y - 0.5, depth - 0.5));
                    gridGeo.vertices.push(new THREE.Vector3(width - 0.5, y - 0.5, -0.5));
                    gridGeo.vertices.push(new THREE.Vector3(width - 0.5, y - 0.5, depth - 0.5));
                }

                var line = new THREE.LineSegments(gridGeo, material);
                this.scene.add(line);
            } else if (d[0] == 'S') { // spawn
                var cube = null;
                if (this.pool.length > 0) {
                    cube = this.pool.pop();
                } else {
                    const geometry = new THREE.BoxGeometry(1, 1, 1);
                    const material = new THREE.MeshPhongMaterial({
                        emissive: 0x072534,
                        side: THREE.DoubleSide,
                        flatShading: true,
                        transparent: true,
                    });
                    cube = new THREE.Mesh(geometry, material);
                    cube.states = []
                    this.scene.add(cube);
                }
                cube.states.push({
                    color: color,
                    opacity: 0,
                    t: frameInfo.number,
                    x: +d[3],
                    y: +d[4],
                    z: +d[5],
                });
                cube.states.push({
                    ...cube.states[cube.states.length - 1],
                    opacity: 1,
                    t: frameInfo.number + +d[2]
                });
                cube.states.push({
                    ...cube.states[cube.states.length - 1],
                    y: d[4] - d[6],
                    t: frameInfo.number + 1
                });
                this.cubes[+d[1]] = cube;
            } else if (d[0] == 'R') { // remove
                const cube = this.cubes[+d[1]];
                cube.states.push({
                    ...cube.states[cube.states.length - 1],
                    t: frameInfo.number
                });
                cube.states.push({
                    ...cube.states[cube.states.length - 1],
                    opacity: 0,
                    t: frameInfo.number + +d[2]
                });
                this.pool.push(cube);
            } else if (d[0] == 'M') { // move
                const cube = this.cubes[+d[1]];
                cube.states.push({
                    ...cube.states[cube.states.length - 1],
                    t: frameInfo.number + +d[3]
                });
                cube.states.push({
                    ...cube.states[cube.states.length - 1],
                    y: +d[2],
                    t: frameInfo.number + +d[4]
                });
            }
        });

        return frameInfo;
    }

    handleGlobalData(players, globalData) {
        this.container = document.createElement('div');
        const viewer = document.body.children[0].children[1].children[0];
        const parent = viewer.parentElement
        parent.replaceChild(this.container, viewer);

        this.container.style.width = window.innerWidth;
        this.container.style.height = window.innerWidth * 9 / 16;

        this.camera = new THREE.PerspectiveCamera(75, 16 / 9, 0.1, 1000);
        this.renderer.setSize(window.innerWidth, window.innerWidth * 9 / 16);
        this.container.appendChild(this.renderer.domElement);

        const renderer = this.renderer;
        const scene = this.scene;
        const camera = this.camera;
        const animate = function() {
            requestAnimationFrame(animate);
            renderer.render(scene, camera);
        };
        const controls = new orbit.OrbitControls(camera, renderer.domElement);

        camera.position.set(-12, 17, 11);
        controls.update();

        const lights = [];
        lights[0] = new THREE.PointLight(0xffffff, 1, 0);
        lights[1] = new THREE.PointLight(0xffffff, 1, 0);
        lights[2] = new THREE.PointLight(0xffffff, 1, 0);

        lights[0].position.set(0, 200, 0);
        lights[1].position.set(100, 200, 100);
        lights[2].position.set(-100, -200, -100);

        scene.add(lights[0]);
        scene.add(lights[1]);
        scene.add(lights[2]);

        animate();
    }
}