
"use client";

import React, { useEffect, useRef, useState } from "react";
import * as THREE from "three";
import { 
  Download, LayoutTemplate, Layers, BrainCircuit, DownloadCloud, ChevronRight,
  Languages, Mic, WifiOff, Cpu, Monitor, Lightbulb, Plug, Wind, Fan, Code, Droplet, 
  SmartphoneCharging, Paintbrush, Check, Globe
} from "lucide-react";

export default function HomePage() {
  const containerRef = useRef<HTMLDivElement>(null);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [showCreatorPopup, setShowCreatorPopup] = useState(false);
  const [isFollowing, setIsFollowing] = useState(false);

  // Randomly trigger the Creator Profile popup and show for exactly 24s
  useEffect(() => {
    let hideTimer: NodeJS.Timeout;
    const randomInitialDelay = Math.floor(Math.random() * 3000) + 2000; // 2-5s initial random delay

    const showTimer = setTimeout(() => {
      setShowCreatorPopup(true);

      hideTimer = setTimeout(() => {
        setShowCreatorPopup(false);
      }, 24000); // Stays visible for exactly 24 seconds
    }, randomInitialDelay);

    return () => {
      clearTimeout(showTimer);
      if (hideTimer) clearTimeout(hideTimer);
    };
  }, []);

  useEffect(() => {
    if (!containerRef.current) return;

    const phoneWidth = 3.10;
    const phoneHeight = 6.8;
    const phoneDepth = 0.33;
    const cornerRadius = 0.58;

    const container = containerRef.current;
    const scene = new THREE.Scene();
    scene.background = null;

    const camera = new THREE.PerspectiveCamera(35, window.innerWidth / window.innerHeight, 0.1, 100);
    camera.position.set(0, 0, 10);
    camera.lookAt(0, 0, 0);

    const renderer = new THREE.WebGLRenderer({ 
      antialias: true, 
      alpha: true, 
      powerPreference: "high-performance",
      precision: "mediump"
    });
    const pixelRatio = typeof window !== "undefined" ? Math.min(window.devicePixelRatio || 1, 1.5) : 1;
    renderer.setPixelRatio(pixelRatio);
    renderer.shadowMap.enabled = false;
    renderer.toneMapping = THREE.ACESFilmicToneMapping;
    renderer.toneMappingExposure = 1.1;
    renderer.outputEncoding = THREE.sRGBEncoding;
    renderer.setSize(window.innerWidth, window.innerHeight);

    container.innerHTML = "";
    container.appendChild(renderer.domElement);

    // Realistic Studio HDR Environment Map for Metallic & Glass Reflections
    const envCanvas = document.createElement("canvas");
    envCanvas.width = 1024;
    envCanvas.height = 512;
    const envCtx = envCanvas.getContext("2d")!;
    
    // Deep studio gradient background
    const bgGrad = envCtx.createLinearGradient(0, 0, 0, 512);
    bgGrad.addColorStop(0, "#303e54");
    bgGrad.addColorStop(0.35, "#151d2a");
    bgGrad.addColorStop(0.65, "#080c14");
    bgGrad.addColorStop(1, "#1c2636");
    envCtx.fillStyle = bgGrad;
    envCtx.fillRect(0, 0, 1024, 512);
    
    // Top Studio Overhead Softbox (Specular key reflection along top bevel)
    const topSoftbox = envCtx.createRadialGradient(512, 80, 10, 512, 80, 360);
    topSoftbox.addColorStop(0, "rgba(255, 255, 255, 1.0)");
    topSoftbox.addColorStop(0.25, "rgba(235, 245, 255, 0.9)");
    topSoftbox.addColorStop(0.6, "rgba(180, 210, 255, 0.35)");
    topSoftbox.addColorStop(1, "rgba(0, 0, 0, 0)");
    envCtx.fillStyle = topSoftbox;
    envCtx.fillRect(0, 0, 1024, 300);

    // Left Rim Softbox (Long vertical highlight strip for sharp edge reflection)
    const leftStrip = envCtx.createLinearGradient(120, 0, 280, 0);
    leftStrip.addColorStop(0, "rgba(0, 0, 0, 0)");
    leftStrip.addColorStop(0.3, "rgba(255, 255, 255, 0.95)");
    leftStrip.addColorStop(0.7, "rgba(255, 255, 255, 0.95)");
    leftStrip.addColorStop(1, "rgba(0, 0, 0, 0)");
    envCtx.fillStyle = leftStrip;
    envCtx.fillRect(100, 30, 200, 440);

    // Right Rim Softbox (Sleek edge shine for buttons and right rail)
    const rightStrip = envCtx.createLinearGradient(740, 0, 900, 0);
    rightStrip.addColorStop(0, "rgba(0, 0, 0, 0)");
    rightStrip.addColorStop(0.3, "rgba(245, 250, 255, 0.9)");
    rightStrip.addColorStop(0.7, "rgba(245, 250, 255, 0.9)");
    rightStrip.addColorStop(1, "rgba(0, 0, 0, 0)");
    envCtx.fillStyle = rightStrip;
    envCtx.fillRect(730, 30, 200, 440);

    // Ground bounce reflection
    const groundBounce = envCtx.createRadialGradient(512, 490, 10, 512, 490, 280);
    groundBounce.addColorStop(0, "rgba(200, 225, 255, 0.65)");
    groundBounce.addColorStop(1, "rgba(0, 0, 0, 0)");
    envCtx.fillStyle = groundBounce;
    envCtx.fillRect(200, 360, 624, 152);

    const envTexture = new THREE.CanvasTexture(envCanvas);
    envTexture.mapping = THREE.EquirectangularReflectionMapping;
    envTexture.encoding = THREE.sRGBEncoding;

    const pmremGenerator = new THREE.PMREMGenerator(renderer);
    pmremGenerator.compileEquirectangularShader();
    const envMap = pmremGenerator.fromEquirectangular(envTexture).texture;
    scene.environment = envMap;

    const ambientLight = new THREE.AmbientLight(0xffffff, 0.45);
    scene.add(ambientLight);

    const keyLight = new THREE.DirectionalLight(0xffffff, 2.6);
    keyLight.position.set(5, 9, 6);
    keyLight.castShadow = true;
    keyLight.shadow.mapSize.width = 2048;
    keyLight.shadow.mapSize.height = 2048;
    keyLight.shadow.bias = -0.0001;
    scene.add(keyLight);

    const fillLight = new THREE.DirectionalLight(0xe4edff, 1.2);
    fillLight.position.set(-6, -3, 6);
    scene.add(fillLight);

    const rimLight = new THREE.SpotLight(0xffffff, 4.2);
    rimLight.position.set(5, 7, -8);
    rimLight.lookAt(0, 0, 0);
    rimLight.penumbra = 0.4;
    scene.add(rimLight);

    const rimLight2 = new THREE.DirectionalLight(0xddeeff, 2.0);
    rimLight2.position.set(-6, 6, -8);
    scene.add(rimLight2);

    // Specular glint light across metallic chamfer
    const specularGlint = new THREE.PointLight(0xffffff, 2.0, 15);
    specularGlint.position.set(2, 4, 3);
    scene.add(specularGlint);

    const screenCanvas = document.createElement("canvas");
    screenCanvas.width = 1200;
    screenCanvas.height = 2600;
    const sCtx = screenCanvas.getContext("2d")!;

    const grad = sCtx.createLinearGradient(0, 0, 0, 2600);
    grad.addColorStop(0, "#1c2e4a");
    grad.addColorStop(0.5, "#0d1b2a");
    grad.addColorStop(1, "#0b131e");

    const screenTex = new THREE.CanvasTexture(screenCanvas);
    screenTex.encoding = THREE.sRGBEncoding;
    screenTex.minFilter = THREE.LinearFilter;
    screenTex.generateMipmaps = false;

    let wallpaperLoaded = false;
    const wallpaperImg = new Image();
    wallpaperImg.crossOrigin = "anonymous";
    wallpaperImg.onload = () => {
      wallpaperLoaded = true;
      renderScreenComposite();
    };
    wallpaperImg.src = 'https://hgbpavdzzdnwhvzdvybh.supabase.co/storage/v1/object/sign/hire_files/1788991331724_k7ro1m.jpg?token=eyJraWQiOiIwOGJjNTQ2MC1mMzU0LTQyMDMtOTg4YS0yMGMzYThkMGU1YTIiLCJhbGciOiJIUzI1NiJ9.eyJ1cmwiOiJoaXJlX2ZpbGVzLzE3ODg5OTEzMzE3MjRfazdybzFtLmpwZyIsInNjb3BlIjoiZG93bmxvYWQiLCJpYXQiOjE3ODg5OTE5MjQsImV4cCI6MTgyMDUyNzkyNH0.JCmeQXCOj17rU0AbjkVYfh59bxLj-zcpwNixgtSIisY';

    function renderScreenComposite() {
      sCtx.clearRect(0, 0, screenCanvas.width, screenCanvas.height);
      if (wallpaperLoaded) {
        const imgAspect = wallpaperImg.width / wallpaperImg.height;
        const canvasAspect = screenCanvas.width / screenCanvas.height;
        let drawWidth = screenCanvas.width;
        let drawHeight = screenCanvas.height;
        let offsetX = 0;
        let offsetY = 0;
        if (imgAspect > canvasAspect) {
          drawWidth = drawHeight * imgAspect;
          offsetX = -(drawWidth - screenCanvas.width) / 2;
        } else {
          drawHeight = drawWidth / imgAspect;
          offsetY = -(drawHeight - screenCanvas.height) / 2;
        }
        sCtx.drawImage(wallpaperImg, offsetX, offsetY, drawWidth, drawHeight);
      } else {
        sCtx.fillStyle = grad;
        sCtx.fillRect(0, 0, screenCanvas.width, screenCanvas.height);
      }
      screenTex.needsUpdate = true;
      if (materials && materials.screen) {
        materials.screen.map = screenTex;
        materials.screen.needsUpdate = true;
      }
    }

    const colors = { 
      base: 0x181e2b, 
      matte: 0x0e121a, 
      bump: 0x141a26, 
      logo: 0x3d4b63, 
      panelLine: 0x090c14 
    };

    const materials = {
      frame: new THREE.MeshPhysicalMaterial({ 
        color: colors.base, 
        metalness: 0.96, 
        roughness: 0.16, 
        clearcoat: 0.9, 
        clearcoatRoughness: 0.1,
        reflectivity: 1.0 
      }),
      backGlass: new THREE.MeshPhysicalMaterial({ 
        color: colors.matte, 
        metalness: 0.2, 
        roughness: 0.4, 
        clearcoat: 0.75, 
        clearcoatRoughness: 0.2 
      }),
      innerBackPanel: new THREE.MeshPhysicalMaterial({ 
        color: colors.matte, 
        metalness: 0.18, 
        roughness: 0.5, 
        clearcoat: 0.6, 
        clearcoatRoughness: 0.25 
      }),
      screen: new THREE.MeshBasicMaterial({ map: screenTex, side: THREE.FrontSide, toneMapped: false }),
      cameraBump: new THREE.MeshPhysicalMaterial({ 
        color: colors.bump, 
        metalness: 0.75, 
        roughness: 0.2, 
        clearcoat: 0.9, 
        clearcoatRoughness: 0.1 
      }),
      lensRingBase: new THREE.MeshPhysicalMaterial({ 
        color: 0x35445c, 
        metalness: 0.98, 
        roughness: 0.08, 
        clearcoat: 1.0, 
        clearcoatRoughness: 0.05 
      }),
      lensInnerBezel: new THREE.MeshPhysicalMaterial({ 
        color: 0x06080e, 
        metalness: 0.85, 
        roughness: 0.35 
      }),
      lensGlass: new THREE.MeshPhysicalMaterial({ 
        color: 0x030408, 
        metalness: 0.2, 
        roughness: 0.0, 
        transparent: true, 
        opacity: 0.65, 
        clearcoat: 1.0, 
        clearcoatRoughness: 0.02 
      }),
      pupilReflection: new THREE.MeshPhysicalMaterial({ 
        color: 0x1d4796, 
        emissive: 0x030a1c, 
        metalness: 0.9, 
        roughness: 0.05, 
        clearcoat: 1.0 
      }),
      logo: new THREE.MeshPhysicalMaterial({ 
        color: colors.logo, 
        metalness: 0.98, 
        roughness: 0.08, 
        clearcoat: 1.0, 
        clearcoatRoughness: 0.05, 
        side: THREE.DoubleSide 
      }),
      antenna: new THREE.MeshBasicMaterial({ color: 0x141822 }),
      blackDetail: new THREE.MeshBasicMaterial({ color: 0x0f1118 }),
      screenBorder: new THREE.MeshPhysicalMaterial({ 
        color: 0x000000, 
        metalness: 0.15, 
        roughness: 0.05, 
        clearcoat: 1.0, 
        clearcoatRoughness: 0.05 
      }),
      buttonMat: new THREE.MeshPhysicalMaterial({ 
        color: colors.base, 
        metalness: 0.98, 
        roughness: 0.12, 
        clearcoat: 0.9, 
        clearcoatRoughness: 0.1 
      }),
      captureBtnMat: new THREE.MeshPhysicalMaterial({ 
        color: 0x0c1018, 
        metalness: 0.95, 
        roughness: 0.15, 
        clearcoat: 1.0, 
        clearcoatRoughness: 0.08 
      })
    };

    renderScreenComposite();

    function createRoundedRectShape(width: number, height: number, radius: number) {
      const shape = new THREE.Shape();
      const x = -width / 2, y = -height / 2;
      shape.moveTo(x, y + radius);
      shape.lineTo(x, y + height - radius);
      shape.quadraticCurveTo(x, y + height, x + radius, y + height);
      shape.lineTo(x + width - radius, y + height);
      shape.quadraticCurveTo(x + width, y + height, x + width, y + height - radius);
      shape.lineTo(x + width, y + radius);
      shape.quadraticCurveTo(x + width, y, x + width - radius, y);
      shape.lineTo(x + radius, y);
      shape.quadraticCurveTo(x, y, x, y + radius);
      return shape;
    }

    function createRoundedPlane(width: number, height: number, radius: number) {
      const shape = createRoundedRectShape(width, height, radius);
      const geometry = new THREE.ShapeGeometry(shape, 24);
      const pos = geometry.attributes.position;
      const x = -width / 2, y = -height / 2;
      const uv = new Float32Array(pos.count * 2);
      for (let i = 0; i < pos.count; i++) {
        uv[i * 2] = (pos.getX(i) - x) / width;
        uv[i * 2 + 1] = (pos.getY(i) - y) / height;
      }
      geometry.setAttribute("uv", new THREE.BufferAttribute(uv, 2));
      return geometry;
    }

    const phoneGroup = new THREE.Group();
    scene.add(phoneGroup);

    const frameGeom = new THREE.ExtrudeGeometry(createRoundedRectShape(phoneWidth, phoneHeight, cornerRadius), { depth: phoneDepth, bevelEnabled: true, bevelSegments: 4, steps: 1, bevelSize: 0.05, bevelThickness: 0.05, curveSegments: 24 });
    frameGeom.translate(0, 0, -phoneDepth / 2);
    const frameMesh = new THREE.Mesh(frameGeom, materials.frame);
    phoneGroup.add(frameMesh);

    const borderMesh = new THREE.Mesh(createRoundedPlane(phoneWidth - 0.06, phoneHeight - 0.06, cornerRadius - 0.02), materials.screenBorder);
    borderMesh.position.z = phoneDepth / 2 + 0.051;
    phoneGroup.add(borderMesh);

    const screenMesh = new THREE.Mesh(createRoundedPlane(phoneWidth - 0.16, phoneHeight - 0.16, cornerRadius - 0.07), materials.screen);
    screenMesh.position.z = phoneDepth / 2 + 0.055;
    phoneGroup.add(screenMesh);

    const islandGeom = new THREE.ExtrudeGeometry(createRoundedRectShape(0.84, 0.24, 0.12), { depth: 0.01, bevelEnabled: false, curveSegments: 16 });
    const islandMesh = new THREE.Mesh(islandGeom, new THREE.MeshBasicMaterial({ color: 0x000000 }));
    islandMesh.position.set(0, phoneHeight / 2 - 0.25, phoneDepth / 2 + 0.058);
    phoneGroup.add(islandMesh);

    const backMesh = new THREE.Mesh(new THREE.ExtrudeGeometry(createRoundedRectShape(phoneWidth - 0.06, phoneHeight - 0.06, cornerRadius - 0.02), { depth: 0.015, bevelEnabled: false, curveSegments: 24 }), materials.backGlass);
    backMesh.position.z = -phoneDepth / 2 - 0.055;
    phoneGroup.add(backMesh);

    const panelGeom = new THREE.ExtrudeGeometry(createRoundedRectShape(phoneWidth - 0.18, 4.70, cornerRadius - 0.05), { depth: 0.008, bevelEnabled: true, bevelSegments: 2, steps: 1, bevelSize: 0.015, bevelThickness: 0.015, curveSegments: 24 });
    const panelMesh = new THREE.Mesh(panelGeom, materials.innerBackPanel);
    panelMesh.rotation.y = Math.PI;
    panelMesh.position.set(0, -0.86, -phoneDepth / 2 - 0.054);
    panelMesh.add(new THREE.LineSegments(new THREE.EdgesGeometry(panelGeom), new THREE.LineBasicMaterial({ color: colors.panelLine, linewidth: 1, transparent: true, opacity: 0.4 })));
    phoneGroup.add(panelMesh);

    const camIslandDepth = 0.08;
    const camIslandBevel = 0.04;
    const camIslandGeom = new THREE.ExtrudeGeometry(createRoundedRectShape(2.80, 1.55, 0.40), { depth: camIslandDepth, bevelEnabled: true, bevelSegments: 6, steps: 1, bevelSize: camIslandBevel, bevelThickness: camIslandBevel, curveSegments: 32 });
    camIslandGeom.translate(0, 0, -camIslandDepth / 2);
    const camIslandMesh = new THREE.Mesh(camIslandGeom, materials.cameraBump);
    const camY = (phoneHeight / 2) - (1.55 / 2) - 0.18;
    const camBumpCenterZ = -0.25;
    camIslandMesh.position.set(0, camY, camBumpCenterZ);
    phoneGroup.add(camIslandMesh);

    const islandSurfaceZ = camBumpCenterZ - (camIslandDepth / 2) - camIslandBevel;

    function createProMaxLens(x: number, y: number, radius: number, surfaceZ: number) {
      const group = new THREE.Group();
      group.position.set(x, y, surfaceZ);
      const collarGeom = new THREE.CylinderGeometry(radius + 0.058, radius + 0.068, 0.025, 32);
      collarGeom.rotateX(Math.PI / 2);
      const collarMesh = new THREE.Mesh(collarGeom, materials.lensRingBase);
      collarMesh.position.z = -0.0125;
      group.add(collarMesh);

      const barrelGeom = new THREE.CylinderGeometry(radius + 0.038, radius + 0.038, 0.09, 32, 1, true);
      barrelGeom.rotateX(Math.PI / 2);
      const barrelMesh = new THREE.Mesh(barrelGeom, materials.lensInnerBezel);
      barrelMesh.position.z = -0.07;
      group.add(barrelMesh);

      const bezelMesh = new THREE.Mesh(new THREE.RingGeometry(radius - 0.05, radius + 0.005, 32), materials.lensInnerBezel);
      bezelMesh.rotation.y = Math.PI;
      bezelMesh.position.z = -0.093;
      group.add(bezelMesh);

      const glassMesh = new THREE.Mesh(new THREE.CircleGeometry(radius - 0.018, 32), materials.lensGlass);
      glassMesh.rotation.y = Math.PI;
      glassMesh.position.z = -0.085;
      group.add(glassMesh);

      const sensorZ = -0.05;
      const aperture = new THREE.Mesh(new THREE.RingGeometry(radius * 0.3, radius * 0.85, 24), new THREE.MeshBasicMaterial({ color: 0x030303 }));
      aperture.rotation.y = Math.PI;
      aperture.position.z = sensorZ;
      group.add(aperture);

      const sensorMesh = new THREE.Mesh(new THREE.CircleGeometry(radius * 0.3, 16), materials.pupilReflection);
      sensorMesh.rotation.y = Math.PI;
      sensorMesh.position.z = sensorZ - 0.001;
      group.add(sensorMesh);

      return group;
    }

    phoneGroup.add(
      createProMaxLens(0.82, camY + 0.35, 0.26, islandSurfaceZ),
      createProMaxLens(0.82, camY - 0.35, 0.26, islandSurfaceZ),
      createProMaxLens(0.14, camY, 0.26, islandSurfaceZ)
    );

    const flashGroup = new THREE.Group();
    flashGroup.position.set(-0.92, camY + 0.42, islandSurfaceZ);
    const flashGlass = new THREE.Mesh(new THREE.CircleGeometry(0.08, 32), new THREE.MeshPhysicalMaterial({ color: 0xffffff, metalness: 0.1, roughness: 0.1, clearcoat: 1.0 }));
    flashGlass.rotation.y = Math.PI;
    flashGlass.position.z = -0.004;
    const flashCenter = new THREE.Mesh(new THREE.CircleGeometry(0.035, 20), new THREE.MeshBasicMaterial({ color: 0xffe6b0 }));
    flashCenter.rotation.y = Math.PI;
    flashCenter.position.z = -0.008;
    flashGroup.add(new THREE.Mesh(new THREE.TorusGeometry(0.08, 0.012, 20, 36), materials.lensRingBase), flashGlass, flashCenter);
    phoneGroup.add(flashGroup);

    const lidarGroup = new THREE.Group();
    lidarGroup.position.set(-0.92, camY - 0.42, islandSurfaceZ);
    const lidarGlass = new THREE.Mesh(new THREE.CircleGeometry(0.095, 32), materials.lensGlass);
    lidarGlass.rotation.y = Math.PI;
    lidarGlass.position.z = -0.004;
    const lidarDot = new THREE.Mesh(new THREE.CircleGeometry(0.035, 20), materials.blackDetail);
    lidarDot.rotation.y = Math.PI;
    lidarDot.position.z = -0.008;
    lidarGroup.add(new THREE.Mesh(new THREE.TorusGeometry(0.095, 0.012, 20, 36), new THREE.MeshPhysicalMaterial({ color: 0x111111, metalness: 0.8, roughness: 0.3 })), lidarGlass, lidarDot);
    phoneGroup.add(lidarGroup);

    const mic = new THREE.Mesh(new THREE.CircleGeometry(0.025, 20), materials.blackDetail);
    mic.rotation.y = Math.PI;
    mic.position.set(-0.6, camY + 0.02, islandSurfaceZ - 0.001);
    phoneGroup.add(mic);

    function createButton(w: number, h: number, d: number, x: number, y: number, z: number, mat = materials.buttonMat, isRecessed = false) {
      const group = new THREE.Group();
      const btnMesh = new THREE.Mesh(new THREE.ExtrudeGeometry(createRoundedRectShape(w, h, w / 2), { depth: d, bevelEnabled: true, bevelSegments: 6, steps: 1, bevelSize: 0.008, bevelThickness: 0.008, curveSegments: 32 }).translate(0, 0, -d / 2), mat);
      btnMesh.castShadow = true;
      group.add(btnMesh);
      const gap = isRecessed ? 0.02 : 0.015;
      const cutoutMesh = new THREE.Mesh(new THREE.ShapeGeometry(createRoundedRectShape(w + gap, h + gap, (w + gap) / 2)), new THREE.MeshBasicMaterial({ color: 0x030406, side: THREE.DoubleSide }));
      cutoutMesh.position.z = -d / 2 + 0.011;
      group.add(cutoutMesh);
      if (x > 0) {
        group.rotation.y = Math.PI / 2;
        group.position.set(x + (isRecessed ? -0.01 : d / 2 - 0.01), y, z);
      } else {
        group.rotation.y = -Math.PI / 2;
        group.position.set(x - (isRecessed ? -0.01 : d / 2 - 0.01), y, z);
      }
      return group;
    }

    phoneGroup.add(
      createButton(0.12, 0.4, 0.04, -phoneWidth / 2, phoneHeight / 2 - 1.2, 0),
      createButton(0.12, 0.65, 0.04, -phoneWidth / 2, phoneHeight / 2 - 2.0, 0),
      createButton(0.12, 0.65, 0.04, -phoneWidth / 2, phoneHeight / 2 - 2.8, 0),
      createButton(0.12, 0.85, 0.04, phoneWidth / 2, phoneHeight / 2 - 1.9, 0),
      createButton(0.16, 0.75, 0.03, phoneWidth / 2, -phoneHeight / 2 + 1.6, 0, materials.captureBtnMat, true)
    );

    const logoGroup = new THREE.Group();
    logoGroup.position.set(0, -0.86, -phoneDepth / 2 - 0.085);
    logoGroup.rotation.y = Math.PI;
    const tx = (x: number) => (x - 12) * 0.052;
    const ty = (y: number) => -(y - 12) * 0.052;
    const logoShape = new THREE.Shape();
    logoShape.moveTo(tx(18.71), ty(19.5));
    logoShape.bezierCurveTo(tx(17.88), ty(20.74), tx(17), ty(21.95), tx(15.66), ty(21.97));
    logoShape.bezierCurveTo(tx(14.32), ty(22), tx(13.89), ty(21.18), tx(12.37), ty(21.18));
    logoShape.bezierCurveTo(tx(10.84), ty(21.18), tx(10.37), ty(21.95), tx(9.09997), ty(22));
    logoShape.bezierCurveTo(tx(7.78997), ty(22.05), tx(6.79997), ty(20.68), tx(5.95997), ty(19.47));
    logoShape.bezierCurveTo(tx(4.24997), ty(17), tx(2.93997), ty(12.45), tx(4.69997), ty(9.39));
    logoShape.bezierCurveTo(tx(5.56997), ty(7.87), tx(7.12997), ty(6.91), tx(8.81997), ty(6.88));
    logoShape.bezierCurveTo(tx(10.1), ty(6.86), tx(11.32), ty(7.75), tx(12.11), ty(7.75));
    logoShape.bezierCurveTo(tx(12.89), ty(7.75), tx(14.37), ty(6.68), tx(15.92), ty(6.84));
    logoShape.bezierCurveTo(tx(16.57), ty(6.87), tx(18.39), ty(7.1), tx(19.56), ty(8.82));
    logoShape.bezierCurveTo(tx(19.47), ty(8.88), tx(17.39), ty(10.1), tx(17.41), ty(12.63));
    logoShape.bezierCurveTo(tx(17.44), ty(15.65), tx(20.06), ty(16.66), tx(20.09), ty(16.67));
    logoShape.bezierCurveTo(tx(20.06), ty(16.74), tx(19.67), ty(18.11), tx(18.71), ty(19.5));
    const leafShape = new THREE.Shape();
    leafShape.moveTo(tx(13), ty(3.5));
    leafShape.bezierCurveTo(tx(13.73), ty(2.67), tx(14.94), ty(2.04), tx(15.94), ty(2));
    leafShape.bezierCurveTo(tx(16.07), ty(3.17), tx(15.6), ty(4.35), tx(14.9), ty(5.19));
    leafShape.bezierCurveTo(tx(14.21), ty(6.04), tx(13.07), ty(6.7), tx(11.95), ty(6.61));
    leafShape.bezierCurveTo(tx(11.8), ty(5.46), tx(12.36), ty(4.26), tx(13), ty(3.5));
    logoGroup.add(new THREE.Mesh(new THREE.ShapeGeometry(logoShape), materials.logo), new THREE.Mesh(new THREE.ShapeGeometry(leafShape), materials.logo));
    phoneGroup.add(logoGroup);

    let baseScale = 1.45;
    let basePosY = -3.85;
    let targetRotY = -Math.PI / 4;
    let targetScale = baseScale;
    let targetPosY = basePosY;
    let currentRotY = targetRotY;
    let currentScale = targetScale;
    let currentPosY = targetPosY;

    function updateResponsiveLayout() {
      const width = window.innerWidth;
      const height = window.innerHeight;
      camera.aspect = width / height;
      camera.updateProjectionMatrix();
      renderer.setSize(width, height);

      if (width < 480) {
        baseScale = 1.0;
        basePosY = -2.5;
      } else if (width < 768) {
        baseScale = 1.15;
        basePosY = -3.2;
      } else if (width < 1024) {
        baseScale = 1.3;
        basePosY = -3.5;
      } else {
        baseScale = 1.45;
        basePosY = -3.85;
      }
      updateScrollState();
    }

    let isVisible = true;
    function updateScrollState() {
      const scrollY = typeof window !== "undefined" ? window.scrollY : 0;
      const scrollRange = window.innerHeight * 1.5;
      const scrollProgress = Math.min(Math.max(scrollY / scrollRange, 0), 1);
      targetRotY = (-Math.PI / 4) * (1 - scrollProgress);
      targetScale = baseScale * (1 - scrollProgress * 0.20);
      targetPosY = basePosY + scrollProgress * (Math.abs(basePosY) - 0.5);
      
      // Pause WebGL rendering when scrolled past the hero view to save 100% GPU/CPU
      isVisible = scrollY < window.innerHeight * 1.4;
    }

    window.addEventListener("scroll", updateScrollState, { passive: true });
    window.addEventListener("resize", updateResponsiveLayout, { passive: true });
    updateResponsiveLayout();

    let reqId: number;
    function animate() {
      reqId = requestAnimationFrame(animate);

      if (!isVisible) return;

      currentRotY += (targetRotY - currentRotY) * 0.08;
      currentScale += (targetScale - currentScale) * 0.08;
      currentPosY += (targetPosY - currentPosY) * 0.08;

      specularGlint.position.x = 2 + Math.sin(currentRotY) * 3;
      specularGlint.position.z = 3 + Math.cos(currentRotY) * 2;

      phoneGroup.rotation.y = currentRotY;
      phoneGroup.rotation.x = 0;
      phoneGroup.scale.set(currentScale, currentScale, currentScale);
      phoneGroup.position.y = currentPosY;

      renderer.render(scene, camera);
    }
    animate();

    return () => {
      cancelAnimationFrame(reqId);
      window.removeEventListener("scroll", updateScrollState);
      window.removeEventListener("resize", updateResponsiveLayout);
      renderer.dispose();
    };
  }, []);

  return (
    <div className="min-h-screen bg-[#f5f5f7] text-[#1d1d1f] antialiased overflow-x-hidden selection:bg-cyan-500 selection:text-black">

    <nav className="fixed top-0 left-0 w-full bg-white/70 backdrop-blur-lg border-b border-zinc-200 z-50 flex flex-col">
        <div className="h-14 flex items-center justify-between px-4 md:px-8 w-full">
            <div className="font-semibold text-lg tracking-tight flex items-center gap-2 text-black">
                <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" strokeWidth="1.25" fill="none" strokeLinecap="round" strokeLinejoin="round" className="w-6 h-6">
                    <circle cx="12" cy="12" r="10" />
                    <clipPath id="nav-sphere-clip">
                        <circle cx="12" cy="12" r="9.5" />
                    </clipPath>
                    <g clipPath="url(#nav-sphere-clip)">
                        <path d="M -2 6 C 6 14, 14 -2, 26 6" />
                        <path d="M -2 12 C 6 20, 14 4, 26 12" />
                        <path d="M -2 18 C 6 26, 14 10, 26 18" />
                    </g>
                </svg>
                Jasica AI
            </div>
            
            <div className="hidden md:flex items-center gap-6 text-sm font-medium text-zinc-500">
                <a href="#specs" className="text-black cursor-pointer group">
                    <span className="underline underline-offset-4 decoration-1 decoration-zinc-400">Overview</span>
                </a>
                <a href="#features" className="hover:text-black cursor-pointer group transition-colors">
                    <span className="underline underline-offset-4 decoration-1 decoration-zinc-300">Features</span>
                </a>
                <a href="/apks/JASICA.apk" download="JASICA.apk" className="bg-black text-white text-xs font-medium px-4 py-1.5 rounded-full hover:bg-zinc-800 transition-colors">
                    Download APK
                </a>
            </div>

            <button onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)} className="md:hidden p-2 text-zinc-600 focus:outline-none cursor-pointer">
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M4 6h16M4 12h16M4 18h16" />
                </svg>
            </button>
        </div>

        {isMobileMenuOpen && (
        <div className="md:hidden w-full bg-white/95 backdrop-blur-lg border-t border-zinc-200 px-4 py-5 flex flex-col gap-4 shadow-xl">
             <a href="#specs" onClick={() => setIsMobileMenuOpen(false)} className="text-black font-medium text-lg">Overview</a>
             <a href="#features" onClick={() => setIsMobileMenuOpen(false)} className="text-zinc-600 font-medium text-lg">Features</a>
             <a href="/apks/JASICA.apk" download="JASICA.apk" onClick={() => setIsMobileMenuOpen(false)} className="bg-black text-white text-center font-medium text-base py-3 rounded-full mt-2">Download APK</a>
        </div>
        )}
    </nav>

    <div className="absolute top-[22vh] sm:top-[20vh] md:top-[18vh] left-0 w-full px-4 text-center z-0 pointer-events-none flex flex-col items-center">
        <h1 className="mobile-title-scale text-[7rem] sm:text-[9rem] md:text-[12rem] lg:text-[16rem] font-bold tracking-apple-tighter leading-[0.8] animate-fade-up pb-2 whitespace-nowrap">
            <span className="text-black">Jasica</span>
            <span className="bg-gradient-to-b from-zinc-600 to-black bg-clip-text text-transparent">AI.</span>
        </h1>
    </div>

    <div className="fixed bottom-10 md:bottom-14 left-0 w-full flex justify-center z-50 pointer-events-none animate-fade-up delay-200">
        <a
            href="/apks/JASICA.apk"
            download="JASICA.apk"
            className="pointer-events-auto bg-black hover:bg-zinc-800 text-white text-lg md:text-xl font-medium px-12 md:px-16 py-4 md:py-5 rounded-full cursor-pointer transition-all duration-200 flex items-center gap-3 shadow-2xl active:scale-95"
        >
            <Download strokeWidth={1} className="w-6 h-6" />
            Download APK
        </a>
    </div>

    <div id="canvas-container" ref={containerRef} />

    <section id="specs" className="relative z-20 bg-[#f5f5f7] py-24 md:py-32 px-4 md:px-12 mt-[100vh] md:mt-[110vh]">
        <div className="max-w-4xl mx-auto">
            <div className="text-center mb-12 md:mb-16">
                <h2 className="text-3xl md:text-5xl font-semibold tracking-tight text-black mb-4">System Specifications</h2>
                <p className="text-lg md:text-xl text-zinc-500 font-normal max-w-2xl mx-auto">Everything you need to know about the latest Android release, structured for clarity and quick access.</p>
            </div>

            <div className="border border-zinc-200 rounded-[1.5rem] md:rounded-[2rem] overflow-hidden bg-white max-w-3xl mx-auto shadow-sm">
                
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-5 sm:py-6 px-6 sm:px-8 border-b border-zinc-200">
                    <div className="flex items-center gap-3 w-full sm:w-1/3 mb-1 sm:mb-0 shrink-0">
                        <div className="w-8 h-8 rounded-[8px] bg-[#007AFF] flex items-center justify-center text-white shrink-0">
                            <LayoutTemplate strokeWidth={1.5} className="w-4 h-4" />
                        </div>
                        <span className="text-zinc-500 font-medium text-base sm:text-lg whitespace-nowrap">App Name</span>
                    </div>
                    <div className="text-zinc-900 font-semibold text-base sm:text-lg sm:text-right w-full sm:w-2/3 pl-11 sm:pl-0 mt-1 sm:mt-0">Jasica AI</div>
                </div>
                
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-5 sm:py-6 px-6 sm:px-8 border-b border-zinc-200">
                    <div className="flex items-center gap-3 w-full sm:w-1/3 mb-1 sm:mb-0 shrink-0">
                        <div className="w-8 h-8 rounded-[8px] bg-[#34C759] flex items-center justify-center text-white shrink-0">
                            <Layers strokeWidth={1.5} className="w-4 h-4" />
                        </div>
                        <span className="text-zinc-500 font-medium text-base sm:text-lg whitespace-nowrap">Platform</span>
                    </div>
                    <div className="text-zinc-900 font-semibold text-base sm:text-lg sm:text-right w-full sm:w-2/3 pl-11 sm:pl-0 mt-1 sm:mt-0">Android OS (6.0+)</div>
                </div>
                
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-5 sm:py-6 px-6 sm:px-8 border-b border-zinc-200">
                    <div className="flex items-center gap-3 w-full sm:w-1/3 mb-1 sm:mb-0 shrink-0">
                        <div className="w-8 h-8 rounded-[8px] bg-[#8E8E93] flex items-center justify-center text-white shrink-0">
                            <BrainCircuit strokeWidth={1.5} className="w-4 h-4" />
                        </div>
                        <span className="text-zinc-500 font-medium text-base sm:text-lg whitespace-nowrap">AI Engine</span>
                    </div>
                    <div className="text-zinc-900 font-semibold text-base sm:text-lg sm:text-right w-full sm:w-2/3 pl-11 sm:pl-0 mt-1 sm:mt-0">Jasica 1.0</div>
                </div>
                
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-5 sm:py-6 px-6 sm:px-8">
                    <div className="flex items-center gap-3 w-full sm:w-1/3 mb-1 sm:mb-0 shrink-0">
                        <div className="w-8 h-8 rounded-[8px] bg-[#5856D6] flex items-center justify-center text-white shrink-0">
                            <DownloadCloud strokeWidth={1.5} className="w-4 h-4" />
                        </div>
                        <span className="text-zinc-500 font-medium text-base sm:text-lg whitespace-nowrap">Direct Link</span>
                    </div>
                    <div className="sm:text-right w-full sm:w-2/3 flex justify-start sm:justify-end pl-11 sm:pl-0 mt-1 sm:mt-0">
                        <a href="/apks/JASICA.apk" download="JASICA.apk" className="text-blue-600 font-semibold text-base sm:text-lg flex items-center gap-2 cursor-pointer group">
                            <span className="underline underline-offset-4 decoration-1">Download App (.apk)</span>
                            <ChevronRight strokeWidth={1.5} className="w-4 h-4 text-blue-600 group-hover:translate-x-0.5 transition-transform" />
                        </a>
                    </div>
                </div>
                
            </div>
        </div>
    </section>

    <section id="features" className="relative z-20 bg-[#f5f5f7] py-16 md:py-24 px-4 md:px-12">
        <div className="max-w-6xl mx-auto">
            
            <div className="text-center mb-12 md:mb-20">
                <h2 className="text-3xl md:text-5xl font-semibold tracking-tight text-black mb-4">Intelligence, built in.</h2>
                <p className="text-lg md:text-xl text-zinc-500 font-normal max-w-2xl mx-auto">A seamless blend of emotional AI, hardware control, and everyday utility.</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-6 gap-0 md:gap-6 divide-y divide-zinc-300 md:divide-y-0">
                
                {/* 1. AI Voice Assistant */}
                <div className="col-span-1 md:col-span-6 py-10 md:p-12 md:bg-gradient-to-br md:from-[#EDE9FE] md:via-[#F5F3FF] md:to-[#FAF5FF] md:border md:border-[#DDD6FE] md:rounded-[2rem] flex flex-col md:flex-row gap-6 md:gap-10 items-start md:items-center overflow-hidden md:shadow-sm">
                    <div className="flex-1 w-full">
                        <div className="flex items-center gap-3.5 mb-3 md:mb-4">
                            <h3 className="text-2xl sm:text-3xl md:text-4xl font-semibold text-black tracking-tight">Meet Jasica.</h3>
                            <div className="w-10 h-10 rounded-[12px] bg-[#5856D6] flex items-center justify-center text-white shrink-0 shadow-sm">
                                <svg viewBox="0 0 24 24" width="22" height="22" stroke="currentColor" strokeWidth="1.25" fill="none" strokeLinecap="round" strokeLinejoin="round" className="w-5 h-5">
                                    <circle cx="12" cy="12" r="10" />
                                    <clipPath id="card-sphere-clip">
                                        <circle cx="12" cy="12" r="9.5" />
                                    </clipPath>
                                    <g clipPath="url(#card-sphere-clip)">
                                        <path d="M -2 6 C 6 14, 14 -2, 26 6" />
                                        <path d="M -2 12 C 6 20, 14 4, 26 12" />
                                        <path d="M -2 18 C 6 26, 14 10, 26 18" />
                                    </g>
                                </svg>
                            </div>
                        </div>
                        <p className="text-base md:text-lg text-zinc-600 mb-6 md:mb-8 leading-relaxed font-normal max-w-3xl">
                            Powered by Jasica 1.0, she isn't just an assistant—she's an emotionally aware companion with a custom persona dedicated to Bristi. Fluent in English and Bengali, she remembers your context and is always ready with hands-free Wake Word activation.
                        </p>
                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-y-3 md:gap-y-4 gap-x-6">
                            <div className="flex items-center gap-3 bg-white/80 backdrop-blur-xs py-2.5 px-3.5 rounded-xl border border-zinc-200 md:border-purple-200/60 shadow-2xs">
                                <BrainCircuit strokeWidth={1.5} className="w-5 h-5 text-purple-600 shrink-0" />
                                <span className="text-zinc-800 font-medium text-sm">Conversation Memory</span>
                            </div>
                            <div className="flex items-center gap-3 bg-white/80 backdrop-blur-xs py-2.5 px-3.5 rounded-xl border border-zinc-200 md:border-purple-200/60 shadow-2xs">
                                <Languages strokeWidth={1.5} className="w-5 h-5 text-purple-600 shrink-0" />
                                <span className="text-zinc-800 font-medium text-sm">Bilingual TTS</span>
                            </div>
                            <div className="flex items-center gap-3 bg-white/80 backdrop-blur-xs py-2.5 px-3.5 rounded-xl border border-zinc-200 md:border-purple-200/60 shadow-2xs">
                                <Mic strokeWidth={1.5} className="w-5 h-5 text-purple-600 shrink-0" />
                                <span className="text-zinc-800 font-medium text-sm">Voice Calibration</span>
                            </div>
                            <div className="flex items-center gap-3 bg-white/80 backdrop-blur-xs py-2.5 px-3.5 rounded-xl border border-zinc-200 md:border-purple-200/60 shadow-2xs">
                                <WifiOff strokeWidth={1.5} className="w-5 h-5 text-purple-600 shrink-0" />
                                <span className="text-zinc-800 font-medium text-sm">Offline Parser</span>
                            </div>
                        </div>
                    </div>
                </div>

                {/* 2. Smart Home & Hardware */}
                <div className="col-span-1 md:col-span-4 py-10 md:p-10 md:bg-gradient-to-br md:from-[#FFF7ED] md:via-[#FFEDD5] md:to-[#FEF3C7] md:border md:border-[#FED7AA] md:rounded-[2rem] flex flex-col justify-between md:shadow-sm">
                    <div>
                        <div className="flex items-center gap-3 mb-3">
                            <h3 className="text-xl sm:text-2xl font-semibold text-black tracking-tight">Smart Hardware Control</h3>
                            <div className="w-9 h-9 rounded-[10px] bg-[#FF9500] flex items-center justify-center text-white shrink-0 shadow-sm">
                                <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" strokeWidth="1.5" fill="none" strokeLinecap="round" strokeLinejoin="round" className="w-5 h-5">
                                    <rect x="5" y="5" width="14" height="14" rx="3" ry="3" />
                                    <rect x="9" y="9" width="6" height="6" rx="1" ry="1" />
                                    <path d="M12 2v3M12 19v3M2 12h3M19 12h3M9 2v3M15 2v3M9 19v3M15 19v3M2 9h3M19 9h3M2 15h3M19 15h3" />
                                </svg>
                            </div>
                        </div>
                        <p className="text-base text-zinc-600 mb-6 md:mb-8 leading-relaxed font-normal max-w-xl">
                            Command your Arduino or ESP32 systems seamlessly via Dual Bluetooth (Classic & BLE). Utilize local offline intents, mood presets, or the manual control panel for complete mastery over your environment.
                        </p>
                    </div>
                    
                    <div className="grid grid-cols-2 sm:grid-cols-3 gap-2 md:gap-3">
                        <div className="bg-white/90 backdrop-blur-xs border border-zinc-200 md:border-orange-200/70 rounded-full px-3 md:px-4 py-2.5 flex items-center justify-center gap-2 shadow-2xs">
                            <Monitor strokeWidth={1.5} className="w-4 h-4 text-orange-500 shrink-0" />
                            <span className="text-xs md:text-sm font-medium text-zinc-800 truncate">PC Hub</span>
                        </div>
                        <div className="bg-white/90 backdrop-blur-xs border border-zinc-200 md:border-orange-200/70 rounded-full px-3 md:px-4 py-2.5 flex items-center justify-center gap-2 shadow-2xs">
                            <Lightbulb strokeWidth={1.5} className="w-4 h-4 text-orange-500 shrink-0" />
                            <span className="text-xs md:text-sm font-medium text-zinc-800 truncate">RGB Light</span>
                        </div>
                        <div className="bg-white/90 backdrop-blur-xs border border-zinc-200 md:border-orange-200/70 rounded-full px-3 md:px-4 py-2.5 flex items-center justify-center gap-2 shadow-2xs">
                            <Plug strokeWidth={1.5} className="w-4 h-4 text-orange-500 shrink-0" />
                            <span className="text-xs md:text-sm font-medium text-zinc-800 truncate">Smart Plug</span>
                        </div>
                        <div className="bg-white/90 backdrop-blur-xs border border-zinc-200 md:border-orange-200/70 rounded-full px-3 md:px-4 py-2.5 flex items-center justify-center gap-2 shadow-2xs">
                            <Wind strokeWidth={1.5} className="w-4 h-4 text-orange-500 shrink-0" />
                            <span className="text-xs md:text-sm font-medium text-zinc-800 truncate">AC Unit</span>
                        </div>
                        <div className="bg-white/90 backdrop-blur-xs border border-zinc-200 md:border-orange-200/70 rounded-full px-3 md:px-4 py-2.5 flex items-center justify-center gap-2 shadow-2xs">
                            <Fan strokeWidth={1.5} className="w-4 h-4 text-orange-500 shrink-0" />
                            <span className="text-xs md:text-sm font-medium text-zinc-800 truncate">Room Fan</span>
                        </div>
                        <div className="bg-white/90 backdrop-blur-xs border border-zinc-200 md:border-orange-200/70 rounded-full px-3 md:px-4 py-2.5 flex items-center justify-center gap-2 shadow-2xs">
                            <Code strokeWidth={1.5} className="w-4 h-4 text-orange-500 shrink-0" />
                            <span className="text-xs md:text-sm font-medium text-zinc-800 truncate">Arduino Gen</span>
                        </div>
                    </div>
                </div>

                {/* 3. Health & Wellness (Water / Hydration) */}
                <div className="col-span-1 md:col-span-2 py-10 md:p-10 md:bg-gradient-to-br md:from-[#E0F7FA] md:via-[#E0F2FE] md:to-[#F0FDFA] md:border md:border-[#A5F3FC] md:rounded-[2rem] flex flex-col justify-between md:shadow-sm">
                    <div>
                        <div className="flex items-center gap-3 mb-3">
                            <h3 className="text-xl sm:text-2xl font-semibold text-black tracking-tight">Stay Hydrated</h3>
                            <div className="w-9 h-9 rounded-[10px] bg-[#06B6D4] flex items-center justify-center text-white shrink-0 shadow-sm">
                                <Droplet strokeWidth={1.5} className="w-5 h-5 text-white" />
                            </div>
                        </div>
                        <p className="text-base text-zinc-600 leading-relaxed font-normal mb-6 md:mb-8">
                            A dedicated background service ensures you drink water every 30 minutes.
                        </p>
                    </div>
                    <div className="bg-white/90 backdrop-blur-xs rounded-2xl p-5 border border-zinc-200 md:border-cyan-200/80 flex flex-col items-center justify-center text-center h-full min-h-[100px] md:min-h-[120px] shadow-2xs">
                        <SmartphoneCharging strokeWidth={1.5} className="w-6 h-6 md:w-7 md:h-7 text-[#06B6D4] mb-2" />
                        <span className="text-sm font-medium text-zinc-900 block mb-1">Lock Screen Override</span>
                        <span className="text-xs text-zinc-500 font-normal">Wakes device & vibrates</span>
                    </div>
                </div>
                
                {/* 4. System Shortcuts */}
                <div className="col-span-1 md:col-span-3 py-10 md:p-10 md:bg-gradient-to-br md:from-[#EFF6FF] md:via-[#E0F2FE] md:to-[#F0F9FF] md:border md:border-[#BFDBFE] md:rounded-[2rem] flex flex-col justify-between md:shadow-sm">
                    <div>
                        <div className="flex items-center gap-3 mb-3">
                            <h3 className="text-xl sm:text-2xl font-semibold text-black tracking-tight">System Integration</h3>
                            <div className="w-9 h-9 rounded-[10px] bg-[#007AFF] flex items-center justify-center text-white shrink-0 shadow-sm">
                                <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" strokeWidth="1.5" fill="none" strokeLinecap="round" strokeLinejoin="round" className="w-5 h-5">
                                    <rect x="3" y="3" width="18" height="18" rx="5" ry="5" />
                                    <polygon points="10,8 16,12 10,16" />
                                </svg>
                            </div>
                        </div>
                        <p className="text-base text-zinc-600 leading-relaxed font-normal mb-6 md:mb-8">
                            Jasica bridges the gap between AI and your OS. Launch social media, open the camera to snap a photo, or start a YouTube playlist using nothing but your voice.
                        </p>
                    </div>
                    
                    <div className="flex items-center gap-3 flex-wrap mt-auto">
                        <div className="w-11 h-11 rounded-[12px] bg-gradient-to-tr from-[#f09433] via-[#dc2743] to-[#bc1888] flex items-center justify-center shadow-sm hover:scale-105 transition-transform">
                            <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" strokeWidth="1.5" fill="none" strokeLinecap="round" strokeLinejoin="round" className="w-5 h-5 text-white">
                                <rect x="2" y="2" width="20" height="20" rx="5" ry="5" />
                                <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z" />
                                <line x1="17.5" y1="6.5" x2="17.51" y2="6.5" />
                            </svg>
                        </div>
                        <div className="w-11 h-11 rounded-[12px] bg-[#1877F2] flex items-center justify-center shadow-sm hover:scale-105 transition-transform">
                            <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" strokeWidth="1.5" fill="none" strokeLinecap="round" strokeLinejoin="round" className="w-5 h-5 text-white">
                                <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z" />
                            </svg>
                        </div>
                        <div className="w-11 h-11 rounded-[12px] bg-[#0A66C2] flex items-center justify-center shadow-sm hover:scale-105 transition-transform">
                            <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" strokeWidth="1.5" fill="none" strokeLinecap="round" strokeLinejoin="round" className="w-5 h-5 text-white">
                                <path d="M16 8a6 6 0 0 1 6 6v7h-4v-7a2 2 0 0 0-2-2 2 2 0 0 0-2 2v7h-4v-7a6 6 0 0 1 6-6z" />
                                <rect x="2" y="9" width="4" height="12" />
                                <circle cx="4" cy="4" r="2" />
                            </svg>
                        </div>
                        <div className="w-11 h-11 rounded-[12px] bg-[#FA243C] flex items-center justify-center shadow-sm hover:scale-105 transition-transform">
                            <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" strokeWidth="1.5" fill="none" strokeLinecap="round" strokeLinejoin="round" className="w-5 h-5 text-white">
                                <path d="M9 18V5l12-2v13" />
                                <circle cx="6" cy="18" r="3" />
                                <circle cx="18" cy="16" r="3" />
                            </svg>
                        </div>
                        <div className="w-11 h-11 rounded-[12px] bg-gradient-to-b from-[#A5A5A5] to-[#787878] flex items-center justify-center shadow-sm hover:scale-105 transition-transform">
                            <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" strokeWidth="1.5" fill="none" strokeLinecap="round" strokeLinejoin="round" className="w-5 h-5 text-white">
                                <rect x="3" y="6" width="18" height="13" rx="2" />
                                <circle cx="12" cy="12.5" r="3" />
                                <path d="M8 6V4c0-.6.4-1 1-1h6c.6 0 1 .4 1 1v2" />
                            </svg>
                        </div>
                    </div>
                </div>

                {/* 5. UI, UX & Customization */}
                <div className="col-span-1 md:col-span-3 py-10 md:p-10 md:bg-gradient-to-br md:from-[#F0FDF4] md:via-[#ECFDF5] md:to-[#F0FDFA] md:border md:border-[#BBF7D0] md:rounded-[2rem] flex flex-col justify-between md:shadow-sm">
                    <div>
                        <div className="flex items-center gap-3 mb-3">
                            <h3 className="text-xl sm:text-2xl font-semibold text-black tracking-tight">Fluid Experience</h3>
                            <div className="w-9 h-9 rounded-[10px] bg-[#10B981] flex items-center justify-center text-white shrink-0 shadow-sm">
                                <Paintbrush strokeWidth={1.5} className="w-5 h-5" />
                            </div>
                        </div>
                        <p className="text-base text-zinc-600 leading-relaxed font-normal mb-6 md:mb-8">
                            Enjoy dynamic app icons that adapt to preference, beautiful Lottie animations mirroring the AI's state, and cloud-synced configurations directly from joykumbhakar.vercel.app.
                        </p>
                    </div>
                    <div className="space-y-1">
                        <div className="flex items-center justify-between py-2.5 border-b border-zinc-200 md:border-emerald-200/60">
                             <span className="text-sm font-medium text-zinc-800">Animated Voice UI</span>
                             <Check strokeWidth={1.5} className="w-4 h-4 text-[#10B981]" />
                        </div>
                        <div className="flex items-center justify-between py-2.5 border-b border-zinc-200 md:border-emerald-200/60">
                             <span className="text-sm font-medium text-zinc-800">Dynamic App Icons</span>
                             <Check strokeWidth={1.5} className="w-4 h-4 text-[#10B981]" />
                        </div>
                        <div className="flex items-center justify-between py-2.5">
                             <span className="text-sm font-medium text-zinc-800">Cloud Configuration Sync</span>
                             <Check strokeWidth={1.5} className="w-4 h-4 text-[#10B981]" />
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </section>

    <footer className="relative z-20 bg-[#f5f5f7] py-10 md:py-12 px-4 md:px-12 text-xs text-zinc-500 border-t border-zinc-300">
        <div className="max-w-4xl mx-auto">
            <div className="pb-5 border-b border-zinc-300 leading-relaxed font-normal">
                <p className="mb-2">1. JASICA AI is exclusively developed and optimized for devices running Android 6.0 (Marshmallow) or later.</p>
                <p>2. Actual download size and installation time may vary depending on your local network conditions.</p>
            </div>

            <div className="grid grid-cols-2 md:grid-cols-4 gap-8 py-8">
                <div>
                    <h3 className="text-zinc-900 font-semibold mb-3">Explore Jasica</h3>
                    <ul className="space-y-3 font-normal">
                        <li><a href="/apks/JASICA.apk" download="JASICA.apk" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Android App (.apk)</a></li>
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Smart Hardware</a></li>
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Cloud Config</a></li>
                    </ul>
                </div>
                <div>
                    <h3 className="text-zinc-900 font-semibold mb-3">Features</h3>
                    <ul className="space-y-3 font-normal">
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Voice Assistant</a></li>
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Hardware Control</a></li>
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Health Reminders</a></li>
                    </ul>
                </div>
                <div>
                    <h3 className="text-zinc-900 font-semibold mb-3">System</h3>
                    <ul className="space-y-3 font-normal">
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Gemini API</a></li>
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Bluetooth LE</a></li>
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Local Intents</a></li>
                    </ul>
                </div>
                <div>
                    <h3 className="text-zinc-900 font-semibold mb-3">Creator & Support</h3>
                    <div className="mb-4">
                        <div className="flex items-center gap-2.5 mb-2.5">
                            <img src="/thinking2.png" alt="Bristi Kumbhakar" className="w-9 h-9 rounded-full object-cover shrink-0 border border-zinc-200" />
                            <div>
                                <p className="text-zinc-900 font-medium text-[13px]">Bristi Kumbhakar</p>
                                <p className="text-[11px] text-zinc-500">Current Status: Intern</p>
                            </div>
                        </div>
                        <p className="text-[11px] text-zinc-500 leading-relaxed">
                            Electrician, ITI, Electronics and Telecommunication Engineering from Durgapur Govt. ITI Collage.
                        </p>
                    </div>
                    <ul className="space-y-3 font-normal mt-4 pt-4 border-t border-zinc-200">
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Arduino Generator</a></li>
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Release Notes</a></li>
                    </ul>
                </div>
            </div>

            <div className="pt-6 border-t border-zinc-300 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                <div className="flex flex-col xl:flex-row items-start xl:items-center gap-2 xl:gap-8 font-normal">
                    <p>Copyright &copy; 2026 Bristi / Jasica AI. All rights reserved.</p>
                    <div className="flex flex-wrap items-center gap-x-3 gap-y-2">
                        <a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Privacy Policy</a>
                        <span className="text-zinc-300 hidden sm:inline">|</span>
                        <a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Hardware Safety</a>
                        <span className="text-zinc-300 hidden sm:inline">|</span>
                        <a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">API Terms</a>
                    </div>
                </div>
                <div className="flex items-center gap-2 font-medium text-zinc-600">
                    <Globe strokeWidth={1.5} className="w-4 h-4" />
                    <span>India</span>
                </div>
            </div>
        </div>
    </footer>

    {/* Right-side iOS Style Creator Profile Pop-up (Shows for 24s) */}
    <div
      className={`fixed bottom-24 right-4 md:bottom-8 md:right-8 z-50 transition-all duration-500 ease-out transform ${
        showCreatorPopup
          ? "translate-x-0 opacity-100 scale-100 pointer-events-auto"
          : "translate-x-[120%] opacity-0 scale-95 pointer-events-none"
      }`}
    >
      <div className="bg-white/95 backdrop-blur-2xl border border-zinc-200/90 shadow-[0_20px_60px_rgba(0,0,0,0.18)] rounded-2xl md:rounded-[24px] p-4 md:p-5 max-w-[320px] sm:max-w-sm flex flex-col gap-3 relative">
        {/* Close Button */}
        <button
          onClick={() => setShowCreatorPopup(false)}
          className="absolute top-3 right-3 text-zinc-400 hover:text-zinc-600 p-1 rounded-full hover:bg-zinc-100 transition-colors cursor-pointer"
          aria-label="Close"
        >
          <svg viewBox="0 0 24 24" width="15" height="15" stroke="currentColor" strokeWidth="2" fill="none" strokeLinecap="round" strokeLinejoin="round">
            <line x1="18" y1="6" x2="6" y2="18" />
            <line x1="6" y1="6" x2="18" y2="18" />
          </svg>
        </button>

        {/* Profile Info */}
        <div className="flex items-start gap-3 pr-4">
          <img
            src="/thinking2.png"
            alt="Bristi Kumbhakar"
            className="w-12 h-12 rounded-full object-cover shrink-0 border border-zinc-200/80 shadow-xs"
          />
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-1.5 flex-wrap">
              <h4 className="text-zinc-900 font-semibold text-sm leading-tight truncate">
                Bristi Kumbhakar
              </h4>
              <span className="inline-flex items-center px-1.5 py-0.5 rounded-full text-[10px] font-medium bg-blue-50 text-blue-700 border border-blue-200/60">
                Creator
              </span>
            </div>
            <p className="text-[11px] text-zinc-500 font-normal mt-0.5">
              Current Status: Intern
            </p>
            <p className="text-[11px] text-zinc-600 leading-tight mt-1 line-clamp-2">
              Electrician, ITI, Electronics and Telecommunication Eng. from Durgapur Govt. ITI Collage.
            </p>
          </div>
        </div>

        {/* Follow / Unfollow iOS Action Row */}
        <div className="flex items-center justify-between pt-2.5 border-t border-zinc-100 mt-0.5">
          <span className="text-[11px] text-zinc-400 font-normal">
            {isFollowing ? "Connected with Bristi" : "Follow Creator"}
          </span>
          <button
            onClick={() => setIsFollowing(!isFollowing)}
            className={`font-semibold text-xs px-4 py-1.5 rounded-full transition-all duration-200 active:scale-95 cursor-pointer shadow-xs ${
              isFollowing
                ? "bg-zinc-100 hover:bg-zinc-200 text-zinc-700 border border-zinc-300"
                : "bg-[#007AFF] hover:bg-blue-600 text-white shadow-blue-500/25 shadow-sm"
            }`}
          >
            {isFollowing ? "Unfollow" : "Follow"}
          </button>
        </div>
      </div>
    </div>

    </div>
  );
}
