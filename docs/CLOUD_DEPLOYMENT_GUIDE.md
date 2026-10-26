# CU Daily Schedule — Cloud Deployment Guide

This guide explains how to deploy the **Java 21 Spring Boot + Vaadin Flow** web application to obtain a free, live, public HTTPS URL.

---

## 1. Why Vercel Cannot Host Java / Spring Boot

**Vercel is a serverless platform built specifically for frontend JavaScript/TypeScript frameworks** (Next.js, React, Astro, Vue) and stateless serverless functions (Node.js, Go, Python).

- **No JVM**: Vercel does not support Java, Maven, or JVM containers.
- **Stateful Backend**: Vaadin Flow relies on a persistent Java server (Spring Boot with embedded Tomcat) with active HTTP sessions and WebSockets for real-time UI synchronization and background timers.
- **No Long-Running Daemons**: Serverless functions on Vercel spin down after short request cycles, which terminates Spring Boot applications immediately.

---

## 2. Recommended Free Cloud Platforms for Java 21

The repository is pre-configured with `Dockerfile`, `render.yaml`, `Procfile`, and `system.properties` for immediate 1-click cloud deployment:

### Option A: Render.com (Recommended Free Tier)

1. Sign up or log in at **[render.com](https://render.com/)**.
2. Click **New +** > **Web Service**.
3. Select **Build and deploy from a Git repository** and connect your GitHub repo:
   `https://github.com/alokyadva26/cu_dalily_schedule`
4. Render will auto-detect the [`Dockerfile`](../Dockerfile) or `render.yaml`:
   - **Environment**: Docker
   - **Plan**: Free
5. (Optional) In the **Environment Variables** section, set your Supabase database credentials:
   - `SUPABASE_DB_URL`
   - `SUPABASE_DB_USER`
   - `SUPABASE_DB_PASSWORD`
   *(If omitted, the app automatically runs in Offline Fallback Mode).*
6. Click **Create Web Service**.
7. Once build completes (~2 minutes), Render assigns a public HTTPS URL:
   `https://cu-daily-schedule-xxxx.onrender.com`

---

### Option B: Railway.app (Zero-Config)

1. Go to **[railway.app](https://railway.app/)** and log in with GitHub.
2. Click **New Project** > **Deploy from GitHub repo**.
3. Choose `alokyadva26/cu_dalily_schedule`.
4. Railway automatically detects Maven and Java 21 via `system.properties` and builds the project.
5. In project settings, click **Generate Domain** to get a public HTTPS link:
   `https://cu-daily-schedule-production.up.railway.app`

---

### Option C: Koyeb (Free Tier)

1. Go to **[koyeb.com](https://www.koyeb.com/)**.
2. Create a new service from your GitHub repository with Docker runtime.
3. Deploy and receive your free public `.koyeb.app` URL.
