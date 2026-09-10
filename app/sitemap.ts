import { MetadataRoute } from 'next'

export default function sitemap(): MetadataRoute.Sitemap {
  return [
    { url: 'https://jasicaai.vercel.app', lastModified: new Date() },
    { url: 'https://jasicaai.vercel.app/privacy-policy', lastModified: new Date() },
    { url: 'https://jasicaai.vercel.app/hardware-safety', lastModified: new Date() },
    { url: 'https://jasicaai.vercel.app/api-terms', lastModified: new Date() },
    { url: 'https://jasicaai.vercel.app/docs/smart-hardware', lastModified: new Date() },
    { url: 'https://jasicaai.vercel.app/docs/cloud-config', lastModified: new Date() },
    { url: 'https://jasicaai.vercel.app/docs/arduino-generator', lastModified: new Date() },
    { url: 'https://jasicaai.vercel.app/docs/release-notes', lastModified: new Date() }
  ]
}
