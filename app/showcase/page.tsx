"use client";

import React, { useEffect, useRef } from "react";
import Navbar from "@/components/Navbar";
import Link from "next/link";
import * as THREE from "three";
import { Download, ChevronRight, Sparkles, Info, AppWindow, Cpu, ShieldCheck } from "lucide-react";

export default function ShowcasePage() {
  const containerRef = useRef<HTMLDivElement>(null);

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

    const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true, powerPreference: "high-performance" });
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    renderer.shadowMap.enabled = true;
    renderer.shadowMap.type = THREE.PCFSoftShadowMap;
    renderer.toneMapping = THREE.ACESFilmicToneMapping;
    renderer.toneMappingExposure = 1.1;
    renderer.setSize(window.innerWidth, window.innerHeight);

    container.innerHTML = "";
    container.appendChild(renderer.domElement);

    const ambientLight = new THREE.AmbientLight(0xffffff, 0.3);
    scene.add(ambientLight);

    const keyLight = new THREE.DirectionalLight(0xffffff, 2.2);
    keyLight.position.set(5, 8, 5);
    keyLight.castShadow = true;
    keyLight.shadow.mapSize.width = 2048;
    keyLight.shadow.mapSize.height = 2048;
    keyLight.shadow.bias = -0.0001;
    scene.add(keyLight);

    const fillLight = new THREE.DirectionalLight(0xeef2ff, 0.7);
    fillLight.position.set(-6, -4, 5);
    scene.add(fillLight);

    const rimLight = new THREE.SpotLight(0xffffff, 3.5);
    rimLight.position.set(4, 6, -8);
    rimLight.lookAt(0, 0, 0);
    rimLight.penumbra = 0.5;
    scene.add(rimLight);

    const rimLight2 = new THREE.DirectionalLight(0xffffff, 1.2);
    rimLight2.position.set(-5, 5, -8);
    scene.add(rimLight2);

    const screenCanvas = document.createElement("canvas");
    screenCanvas.width = 1200;
    screenCanvas.height = 2600;
    const sCtx = screenCanvas.getContext("2d")!;

    const grad = sCtx.createLinearGradient(0, 0, 0, 2600);
    grad.addColorStop(0, "#1c2e4a");
    grad.addColorStop(0.5, "#0d1b2a");
    grad.addColorStop(1, "#0b131e");

    const screenTex = new THREE.CanvasTexture(screenCanvas);
    screenTex.minFilter = THREE.LinearFilter;
    screenTex.generateMipmaps = false;

    let wallpaperLoaded = false;
    const wallpaperImg = new Image();
    wallpaperImg.crossOrigin = "anonymous";
    wallpaperImg.onload = () => {
      wallpaperLoaded = true;
      renderScreenComposite();
    };
    wallpaperImg.src = "/orangeandpurplebg.png";

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
    }

    const colors = { base: 0x111622, matte: 0x0e111a, bump: 0x121724, logo: 0x040508, panelLine: 0x090c14 };
    const materials = {
      frame: new THREE.MeshPhysicalMaterial({ color: colors.base, metalness: 0.85, roughness: 0.35, clearcoat: 0.2, clearcoatRoughness: 0.5 }),
      backGlass: new THREE.MeshPhysicalMaterial({ color: colors.matte, metalness: 0.15, roughness: 0.65, clearcoat: 0.1, clearcoatRoughness: 0.9 }),
      innerBackPanel: new THREE.MeshPhysicalMaterial({ color: colors.matte, metalness: 0.12, roughness: 0.7, clearcoat: 0.05, clearcoatRoughness: 0.95 }),
      screen: new THREE.MeshBasicMaterial({ map: screenTex, side: THREE.FrontSide }),
      cameraBump: new THREE.MeshPhysicalMaterial({ color: colors.bump, metalness: 0.6, roughness: 0.4 }),
      lensRingBase: new THREE.MeshPhysicalMaterial({ color: colors.base, metalness: 0.9, roughness: 0.2 }),
      lensInnerBezel: new THREE.MeshPhysicalMaterial({ color: 0x050505, metalness: 0.8, roughness: 0.5 }),
      lensGlass: new THREE.MeshPhysicalMaterial({ color: 0x020202, metalness: 0.3, roughness: 0.0, transparent: true, opacity: 0.6, clearcoat: 1.0 }),
      pupilReflection: new THREE.MeshPhysicalMaterial({ color: 0x1a3d82, emissive: 0x020512, metalness: 0.8, roughness: 0.1, clearcoat: 1.0 }),
      screenBorder: new THREE.MeshPhysicalMaterial({ color: 0x000000, metalness: 0.1, roughness: 0.1, clearcoat: 1.0 }),
      buttonMat: new THREE.MeshPhysicalMaterial({ color: colors.base, metalness: 0.95, roughness: 0.2, clearcoat: 0.6 }),
      captureBtnMat: new THREE.MeshPhysicalMaterial({ color: 0x0a0d14, metalness: 0.9, roughness: 0.1, clearcoat: 1.0 })
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
      const geometry = new THREE.ShapeGeometry(shape, 48);
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

    const frameGeom = new THREE.ExtrudeGeometry(createRoundedRectShape(phoneWidth, phoneHeight, cornerRadius), { depth: phoneDepth, bevelEnabled: true, bevelSegments: 8, steps: 1, bevelSize: 0.05, bevelThickness: 0.05, curveSegments: 48 });
    frameGeom.translate(0, 0, -phoneDepth / 2);
    const frameMesh = new THREE.Mesh(frameGeom, materials.frame);
    frameMesh.castShadow = true;
    phoneGroup.add(frameMesh);

    const borderMesh = new THREE.Mesh(createRoundedPlane(phoneWidth - 0.06, phoneHeight - 0.06, cornerRadius - 0.02), materials.screenBorder);
    borderMesh.position.z = phoneDepth / 2 + 0.051;
    phoneGroup.add(borderMesh);

    const screenMesh = new THREE.Mesh(createRoundedPlane(phoneWidth - 0.16, phoneHeight - 0.16, cornerRadius - 0.07), materials.screen);
    screenMesh.position.z = phoneDepth / 2 + 0.055;
    phoneGroup.add(screenMesh);

    const backMesh = new THREE.Mesh(new THREE.ExtrudeGeometry(createRoundedRectShape(phoneWidth - 0.06, phoneHeight - 0.06, cornerRadius - 0.02), { depth: 0.015, bevelEnabled: false, curveSegments: 48 }), materials.backGlass);
    backMesh.position.z = -phoneDepth / 2 - 0.055;
    phoneGroup.add(backMesh);

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

    function updateScrollState() {
      const scrollRange = window.innerHeight * 1.5;
      const scrollProgress = Math.min(Math.max(window.scrollY / scrollRange, 0), 1);
      targetRotY = (-Math.PI / 4) * (1 - scrollProgress);
      targetScale = baseScale * (1 - scrollProgress * 0.20);
      targetPosY = basePosY + scrollProgress * (Math.abs(basePosY) - 0.5);
    }

    window.addEventListener("scroll", updateScrollState);
    window.addEventListener("resize", updateResponsiveLayout);
    updateResponsiveLayout();

    let reqId: number;
    function animate() {
      reqId = requestAnimationFrame(animate);
      currentRotY += (targetRotY - currentRotY) * 0.08;
      currentScale += (targetScale - currentScale) * 0.08;
      currentPosY += (targetPosY - currentPosY) * 0.08;

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
    <div className="min-h-screen bg-[#f5f5f7] text-[#1d1d1f] font-sans antialiased overflow-x-hidden selection:bg-cyan-500 selection:text-black">
      
      {/* Top Navbar */}
      <nav className="fixed top-0 left-0 w-full h-14 bg-white/70 backdrop-blur-lg border-b border-zinc-200 z-50 flex items-center justify-between px-4 md:px-8">
        <Link href="/" className="font-semibold text-lg tracking-tight flex items-center gap-2 text-black underline underline-offset-4 decoration-black/30">
          <Sparkles className="w-5 h-5 text-cyan-600" strokeWidth={1.5} />
          <span>Jasica AI</span>
        </Link>
        <div className="flex items-center gap-6 text-sm font-medium text-zinc-500">
          <Link href="/" className="text-black underline underline-offset-4 decoration-black/40">Overview</Link>
          <a href="#specs" className="hover:text-black transition underline underline-offset-4 decoration-zinc-400">Tech Specs</a>
          <Link href="/hosting" className="hover:text-black transition underline underline-offset-4 decoration-zinc-400">Custom Hosting</Link>
        </div>
      </nav>

      {/* Hero Title */}
      <div className="absolute top-[18vh] left-0 w-full px-4 text-center z-0 pointer-events-none flex flex-col items-center">
        <h1 className="text-[5.5rem] sm:text-[8rem] md:text-[11rem] lg:text-[14rem] font-bold tracking-tighter leading-[0.85] pb-2 whitespace-nowrap">
          <span className="text-black">Jasica</span>{" "}
          <span className="bg-gradient-to-b from-zinc-600 to-black bg-clip-text text-transparent">AI.</span>
        </h1>
      </div>

      {/* Floating Download Button */}
      <div className="fixed bottom-10 md:bottom-14 left-0 w-full flex justify-center z-40 pointer-events-none">
        <Link
          href="/android"
          className="pointer-events-auto bg-black text-white text-base md:text-lg font-medium px-10 md:px-14 py-3.5 md:py-4 rounded-full transition-transform hover:scale-105 flex items-center gap-3 underline underline-offset-4 decoration-white/40"
        >
          <Download className="w-5 h-5" strokeWidth={1.5} />
          <span>Download Now</span>
        </Link>
      </div>

      {/* 3D Canvas Container */}
      <div ref={containerRef} className="fixed top-0 left-0 w-full h-full z-10 pointer-events-none" />

      {/* Specifications Table Section */}
      <section id="specs" className="relative z-20 bg-[#f5f5f7] min-h-screen py-32 px-4 md:px-12 mt-[110vh]">
        <div className="max-w-4xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-semibold tracking-tight text-black mb-4 underline underline-offset-8 decoration-black/20">
              System Specifications
            </h2>
            <p className="text-xl text-zinc-500 font-normal max-w-2xl mx-auto underline underline-offset-4 decoration-zinc-300">
              Everything you need to know about the latest release, structured for clarity and quick access.
            </p>
          </div>

          <div className="border border-zinc-200 rounded-[2rem] overflow-hidden bg-white max-w-3xl mx-auto">
            {/* Row 1 */}
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-6 px-8 border-b border-zinc-200">
              <div className="flex items-center gap-3 w-1/3 mb-1 sm:mb-0">
                <div className="w-8 h-8 rounded-lg bg-blue-500 flex items-center justify-center text-white shrink-0">
                  <AppWindow className="w-5 h-5" strokeWidth={1.5} />
                </div>
                <span className="text-zinc-500 font-medium text-lg underline underline-offset-2 decoration-zinc-300">App Name</span>
              </div>
              <span className="text-zinc-900 font-semibold text-lg sm:text-right w-full sm:w-2/3 mt-2 sm:mt-0 pl-11 sm:pl-0 underline underline-offset-2 decoration-zinc-400">
                Jasica AI Core
              </span>
            </div>

            {/* Row 2 */}
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-6 px-8 border-b border-zinc-200">
              <div className="flex items-center gap-3 w-1/3 mb-1 sm:mb-0">
                <div className="w-8 h-8 rounded-lg bg-orange-500 flex items-center justify-center text-white shrink-0">
                  <Sparkles className="w-5 h-5" strokeWidth={1.5} />
                </div>
                <span className="text-zinc-500 font-medium text-lg underline underline-offset-2 decoration-zinc-300">Update Name</span>
              </div>
              <span className="text-zinc-900 font-semibold text-lg sm:text-right w-full sm:w-2/3 mt-2 sm:mt-0 pl-11 sm:pl-0 underline underline-offset-2 decoration-zinc-400">
                Neural Hardware Spring Release
              </span>
            </div>

            {/* Row 3 */}
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-6 px-8 border-b border-zinc-200">
              <div className="flex items-center gap-3 w-1/3 mb-1 sm:mb-0">
                <div className="w-8 h-8 rounded-lg bg-zinc-500 flex items-center justify-center text-white shrink-0">
                  <Info className="w-5 h-5" strokeWidth={1.5} />
                </div>
                <span className="text-zinc-500 font-medium text-lg underline underline-offset-2 decoration-zinc-300">Version</span>
              </div>
              <span className="text-zinc-900 font-semibold text-lg sm:text-right w-full sm:w-2/3 mt-2 sm:mt-0 pl-11 sm:pl-0 underline underline-offset-2 decoration-zinc-400">
                v2.0.0 (Next.js App Router)
              </span>
            </div>

            {/* Row 4 */}
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-6 px-8">
              <div className="flex items-center gap-3 w-1/3 mb-1 sm:mb-0">
                <div className="w-8 h-8 rounded-lg bg-green-500 flex items-center justify-center text-white shrink-0">
                  <Download className="w-5 h-5" strokeWidth={1.5} />
                </div>
                <span className="text-zinc-500 font-medium text-lg underline underline-offset-2 decoration-zinc-300">Direct Link</span>
              </div>
              <div className="sm:text-right w-full sm:w-2/3 flex justify-start sm:justify-end mt-2 sm:mt-0 pl-11 sm:pl-0">
                <Link href="/android" className="text-blue-600 font-semibold text-lg flex items-center gap-2 cursor-pointer underline underline-offset-4 decoration-blue-500/50 hover:decoration-blue-600">
                  <span>Download Package (.zip / .apk)</span>
                  <ChevronRight className="w-4 h-4" strokeWidth={1.5} />
                </Link>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="relative z-20 bg-white border-t border-zinc-200 py-10 px-4 md:px-12 text-center">
        <p className="text-zinc-400 text-sm font-medium underline underline-offset-2 decoration-zinc-200">
          Copyright &copy; 2026 Jasica AI Inc. All rights reserved.
        </p>
      </footer>
    </div>
  );
}
