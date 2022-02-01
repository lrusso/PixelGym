const filesToCache = [
	"/",
	"PixelGym.json",
	"PixelGym.png",
	"PixelGymFavIcon_16x16.png",
	"PixelGymFavIcon_192x192.png",
	"PixelGymFavIcon_512x512.png",
	"PixelGymGame.htm",
	"PixelGymGame.js",
	"PixelGymShare.png"
];

const staticCacheName = "pixelgym-v1";

self.addEventListener("install", event => {
	event.waitUntil(
		caches.open(staticCacheName)
		.then(cache => {
			return cache.addAll(filesToCache);
		})
	);
});

self.addEventListener("fetch", event => {
	event.respondWith(
		caches.match(event.request)
		.then(response => {
			if (response) {
				return response;
			}
			return fetch(event.request)
		}).catch(error => {
		})
	);
});