# Limitations and Technical Boundaries

## 1. Supabase Project Host Reachability

### Observation
- Environment file `.env.local` configured:
  ```properties
  NEXT_PUBLIC_SUPABASE_URL=https://hszegowkuthevthdfqym.supabase.co
  NEXT_PUBLIC_SUPABASE_PUBLISHABLE_KEY=sb_publishable_KKbIq7HjrKFv7IopjNBm6w_CITuzlHu
  ```
- Performing DNS resolution on `hszegowkuthevthdfqym.supabase.co` yields `Non-existent domain` (`ENOTFOUND`). This is standard for free-tier Supabase projects that have entered a paused/inactivity state or where a placeholder project reference was specified.
- The Node.js verification test script `test_db.js` encountered the identical DNS resolution failure:
  ```
  TypeError: fetch failed (getaddrinfo ENOTFOUND hszegowkuthevthdfqym.supabase.co)
  ```

### Mitigation & Solution
- The Java application provides **Dual-Mode Operation**:
  1. **Direct JDBC Production Mode**: When valid PostgreSQL host, port, database, user, and password are provided in `.env` or `config.properties`, the application communicates directly with PostgreSQL using `PreparedStatement` transactions.
  2. **Offline / Evaluation Fallback Mode**: When database credentials are not configured or the host is temporarily unreachable, the application falls back gracefully to the bundled seed dataset (`seed.sql` and `timetable.json`), ensuring zero crashes, responsive UI, full timetable browsing, and complete academic demonstration.

---

## 2. Hardware / Web Camera QR Code Scanner

### Observation
- The React application (`QRScanner.tsx`) uses the browser's HTML5 `MediaDevices.getUserMedia()` and the `html5-qrcode` JavaScript library to access the webcam.
- In Java desktop environments, direct camera capture requires native JNI/JNA wrappers (e.g., `webcam-capture` or `OpenCV`) which require OS-specific shared libraries (`.dll`, `.so`, `.dylib`) and can cause compilation or runtime failures on different student evaluation laptops.

### Mitigation & Solution
- To ensure 100% platform portability across all OS environments (Windows, macOS, Linux), the Java application implements an interactive **Faculty QR Badge Scanner Simulator** that decodes employee credentials and integrates with manual Employee ID entry with instant sample selection (`E19761 - Neeraj`, `E16525 - Gitanjali`, `E10279 - Jaspreet`).

---

## 3. Row Level Security (RLS) vs. Direct JDBC Connectivity

### Security Boundary Analysis
- In Supabase, Row Level Security (RLS) policies are evaluated by the PostgREST API layer based on the JWT authorization header.
- Direct PostgreSQL connections over JDBC (`port 5432` or session pooler `port 6543`) connect as a database user (such as `postgres` or an application role).
- Direct connections do not automatically pass Supabase JWT headers unless `SET request.jwt.claim.sub` is executed within the session.
- **Recommendation**: For production deployments, desktop clients should either connect using a restricted PostgreSQL role with `GRANT SELECT ON ...` permissions or interact via an authenticated API endpoint.

---

## 4. Real-time Live Synchronization

- The existing React application does not employ Supabase Realtime WebSockets (`supabase.channel().on(...)`); it uses client-side state, scheduled interval clocks, and a manual/auto refresh button.
- The Java Swing application matches this behavior: it uses background `Timer` and `SwingWorker` threads with a "Refresh" button in the footer and admin dialog to retrieve the latest state.
