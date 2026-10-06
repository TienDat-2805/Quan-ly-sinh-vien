import { defineConfig, type Plugin } from 'vite';
import react from '@vitejs/plugin-react';
import { readFileSync } from 'node:fs';
import { createRequire } from 'node:module';

const requirePackage = createRequire(import.meta.url);
const lucideExports = readFileSync(requirePackage.resolve('lucide-react/dist/esm/lucide-react.js'), 'utf8');
const iconModules = new Map<string, string>();
for (const match of lucideExports.matchAll(/export\s*\{([^}]+)\}\s*from\s*['"]\.\/icons\/([^'"]+)['"]/g)) {
  for (const member of match[1].split(',')) iconModules.set(member.trim().replace(/^default\s+as\s+/, ''), match[2]);
}

// Keep public named imports in application code while loading only the icon modules
// used by this project. This avoids parsing Lucide's entire barrel during Windows builds.
const selectiveIcons: Plugin = {
  name: 'educare-selective-lucide-icons',
  enforce: 'pre',
  transform(code, id) {
    if (!id.replace(/\\/g, '/').includes('/src/') || !code.includes("from 'lucide-react'")) return;
    return {
      code: code.replace(/import\s*\{([^}]+)\}\s*from\s*['"]lucide-react['"];?/g, (_statement: string, imports: string) => imports.split(',').map(member => {
        const [name, alias] = member.trim().split(/\s+as\s+/);
        const filename = iconModules.get(name);
        if (!filename) throw new Error(`Unknown Lucide icon export: ${name}`);
        return `import ${alias || name} from 'lucide-react/dist/esm/icons/${filename}';`;
      }).join('\n')),
      map: null,
    };
  },
};

export default defineConfig({
  plugins: [selectiveIcons, react()],
  base: '/test/',
  server: {
    port: 5173,
    strictPort: true,
    proxy: { '/test/api': { target: 'http://127.0.0.1:8080', changeOrigin: true } },
  },
});
