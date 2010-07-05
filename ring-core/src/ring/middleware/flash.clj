(ns ring.middleware.flash
  "A session-based flash store that persists to the next request.")

(defn wrap-flash-new
  "If a :flash key is set on the response by the handler, a :flash key with
  the same value will be set on the next request that shares the same session.
  This is useful for small messages that persist across redirects."
  [handler]
  (fn [request]
    (let [session  (request :session)
          flash    (session :_flash)
          session  (dissoc session :_flash)
          request  (assoc request
                     :session session
                     :flash flash)
          response (handler request)
          session  (if (contains? response :session)
                       (response :session)                             ;; use user provided session
                       session)                                        ;; or the original
          session  (if-let [flash (response :flash)]
                     (assoc (response :session session) :_flash flash) ;; set _flash into the stored session
                     session)]                                         ;; or use the original for no set flash
          (assoc response :session session))))