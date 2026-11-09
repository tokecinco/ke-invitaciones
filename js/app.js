// KE Invitaciones - Core Application Data & Client Store
(function (global) {
  const STORAGE_KEY = "ke_invitaciones_store_v2";

  // Initial Seed Data
  const defaultData = {
    invitations: [
      {
        id: "valen-15",
        slug: "valen-15",
        title: "Mis 15 Valentina",
        celebrant: "Valentina Gómez",
        eventType: "15 Años",
        date: "2026-12-19T21:00:00",
        dateFormatted: "Sábado 19 de Diciembre de 2026 - 21:00 hs",
        venueName: "Palacio San Martín - Salón Dorado",
        venueAddress: "Av. del Libertador 4500, Palermo, Buenos Aires",
        venueMapsUrl: "https://maps.google.com/?q=Av.+del+Libertador+4500,+Buenos+Aires",
        packageType: "Premium", // "Básico" or "Premium"
        isActive: true,
        welcomeText: "¡Estás invitado a compartir una noche inolvidable junto a mí y mi familia!",
        dressCode: "Elegante - Sugerimos evitar los tonos rosa pastel y dorado (reservados para la agasajada)",
        bankAlias: "VALEN.MIS15.BANCO",
        bankCbu: "0000003100012345678901",
        bankBank: "Banco Galicia - Caja de Ahorro",
        hostWhatsapp: "5491198765432",
        // Nuevas características requeridas:
        spotifyPlaylistUrl: "https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M",
        instagramHashtag: "#Mis15Valen",
        itinerary: [
          { time: "21:00 hs", title: "Recepción & Cóctel", desc: "Bienvenida en el jardín con appetizers y barra de tragos." },
          { time: "22:30 hs", title: "Entrada de la Quinceañera & Vals", desc: "Momento emocionante con Valentina y su familia." },
          { time: "23:15 hs", title: "Cena Principal & Brindis", desc: "Plato principal gourmet y palabras de honor." },
          { time: "00:30 hs", title: "Apertura de Pista & Show", desc: "Show en vivo, DJ set y cotillón temático." },
          { time: "03:00 hs", title: "Torta de 15 & Mesa Dulce", desc: "Torta ceremonial, cascada de chocolate y postres." },
          { time: "05:30 hs", title: "Fin de Fiesta & Desayuno", desc: "Medialunas recién horneadas y café para despedir la noche." }
        ],
        photos: [
          "https://images.unsplash.com/photo-1519741497674-611481863552?w=800&q=80",
          "https://images.unsplash.com/photo-1520854221256-17451cc331bf?w=800&q=80",
          "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?w=800&q=80",
          "https://images.unsplash.com/photo-1465495976277-4387d4b0b4c6?w=800&q=80"
        ],
        price: 35000,
        depositPaid: 17500,
        balancePending: 17500,
        isFullyPaid: false
      },
      {
        id: "boda-sofia-mateo",
        slug: "boda-sofia-mateo",
        title: "Boda Sofía & Mateo",
        celebrant: "Sofía & Mateo",
        eventType: "Casamiento",
        date: "2026-11-28T18:30:00",
        dateFormatted: "Sábado 28 de Noviembre de 2026 - 18:30 hs",
        venueName: "Estancia Las Lilas",
        venueAddress: "Ruta 8 Km 54, Pilar, Buenos Aires",
        venueMapsUrl: "https://maps.google.com/?q=Pilar+Buenos+Aires",
        packageType: "Premium",
        isActive: true,
        welcomeText: "¡Nos casamos y queremos festejarlo con quienes más queremos!",
        dressCode: "Elegante Sport / Formal",
        bankAlias: "BODA.SOFIA.MATEO",
        bankCbu: "0000003100098765432100",
        bankBank: "Santander Río",
        hostWhatsapp: "5491144332211",
        spotifyPlaylistUrl: "https://open.spotify.com/playlist/37i9dQZF1DX4WYpdgoIcn6",
        instagramHashtag: "#BodaSofiYMateo",
        itinerary: [
          { time: "18:30 hs", title: "Ceremonia al Atardecer", desc: "Intercambio de votos en la arboleda de la estancia." },
          { time: "19:45 hs", title: "Cóctel de Bienvenida", desc: "Bebidas y tapeo campestre." },
          { time: "21:30 hs", title: "Cena & Brindis", desc: "Asado gourmet y brindis matrimonial." },
          { time: "23:00 hs", title: "Baile & Fiesta", desc: "Pista iluminada y música hasta el amanecer." }
        ],
        photos: [
          "https://images.unsplash.com/photo-1519741497674-611481863552?w=800&q=80"
        ],
        price: 35000,
        depositPaid: 35000,
        balancePending: 0,
        isFullyPaid: true
      }
    ],
    guests: [
      {
        id: "g-001",
        invitationId: "valen-15",
        name: "Familia Martínez",
        ticketCode: "TKT-VALEN-001",
        seatsAllocated: 4,
        status: "confirmed", // "confirmed", "declined", "pending"
        attendingCount: 4,
        dietary: "1 menú celíaco",
        message: "¡Felices 15 Valen hermosa! No vemos la hora de bailar con vos.",
        checkIn: true,
        checkInTime: "21:15 hs",
        phone: "+54 9 11 3456-7890"
      },
      {
        id: "g-002",
        invitationId: "valen-15",
        name: "Lucas Gómez",
        ticketCode: "TKT-VALEN-002",
        seatsAllocated: 2,
        status: "confirmed",
        attendingCount: 2,
        dietary: "Sin restricciones",
        message: "¡Allí estaremos de gala para festejar!",
        checkIn: false,
        checkInTime: null,
        phone: "+54 9 11 2345-6789"
      },
      {
        id: "g-003",
        invitationId: "valen-15",
        name: "Camila Rodríguez",
        ticketCode: "TKT-VALEN-003",
        seatsAllocated: 1,
        status: "confirmed",
        attendingCount: 1,
        dietary: "Vegetariana",
        message: "¡Te quiero amiga, va a ser la mejor noche del año!",
        checkIn: true,
        checkInTime: "21:40 hs",
        phone: "+54 9 11 9876-5432"
      },
      {
        id: "g-004",
        invitationId: "valen-15",
        name: "Familia Pérez",
        ticketCode: "TKT-VALEN-004",
        seatsAllocated: 3,
        status: "pending",
        attendingCount: 0,
        dietary: "",
        message: "",
        checkIn: false,
        checkInTime: null,
        phone: "+54 9 11 8765-4321"
      },
      {
        id: "g-005",
        invitationId: "valen-15",
        name: "Tomás Fernández",
        ticketCode: "TKT-VALEN-005",
        seatsAllocated: 2,
        status: "confirmed",
        attendingCount: 2,
        dietary: "Sin restricciones",
        message: "¡Muchas felicidades Valen!",
        checkIn: false,
        checkInTime: null,
        phone: "+54 9 11 7654-3210"
      }
    ]
  };

  function loadData() {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (stored) {
        return JSON.parse(stored);
      }
    } catch (e) {
      console.warn("Error loading localStorage", e);
    }
    saveData(defaultData);
    return defaultData;
  }

  function saveData(data) {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
    } catch (e) {
      console.warn("Error saving localStorage", e);
    }
  }

  const Store = {
    getData: loadData,
    saveData: saveData,

    getInvitation: function (idOrSlug) {
      const data = loadData();
      return data.invitations.find(i => i.id === idOrSlug || i.slug === idOrSlug) || data.invitations[0];
    },

    saveInvitation: function (invitation) {
      const data = loadData();
      const idx = data.invitations.findIndex(i => i.id === invitation.id);
      if (idx >= 0) {
        data.invitations[idx] = { ...data.invitations[idx], ...invitation };
      } else {
        data.invitations.push(invitation);
      }
      saveData(data);
      return data.invitations;
    },

    getGuests: function (invitationId) {
      const data = loadData();
      return data.guests.filter(g => !invitationId || g.invitationId === invitationId);
    },

    getGuestByTicket: function (ticketCode) {
      const data = loadData();
      return data.guests.find(g => g.ticketCode.toUpperCase() === ticketCode.toUpperCase().trim());
    },

    toggleCheckIn: function (guestId) {
      const data = loadData();
      const guest = data.guests.find(g => g.id === guestId);
      if (guest) {
        guest.checkIn = !guest.checkIn;
        if (guest.checkIn) {
          const now = new Date();
          guest.checkInTime = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + " hs";
        } else {
          guest.checkInTime = null;
        }
        saveData(data);
      }
      return guest;
    },

    addGuest: function (guestData) {
      const data = loadData();
      const newGuest = {
        id: "g-" + Date.now().toString(36),
        status: "pending",
        attendingCount: 0,
        checkIn: false,
        checkInTime: null,
        ...guestData
      };
      data.guests.push(newGuest);
      saveData(data);
      return newGuest;
    },

    submitRsvp: function (invitationId, guestName, isAttending, seats, dietary, message) {
      const data = loadData();
      let guest = data.guests.find(g => g.invitationId === invitationId && g.name.toLowerCase() === guestName.toLowerCase());
      if (!guest) {
        // create new guest record
        guest = {
          id: "g-" + Date.now().toString(36),
          invitationId: invitationId,
          name: guestName,
          ticketCode: "TKT-" + Math.floor(1000 + Math.random() * 9000),
          seatsAllocated: seats || 1,
          checkIn: false,
          checkInTime: null,
          phone: ""
        };
        data.guests.push(guest);
      }
      guest.status = isAttending ? "confirmed" : "declined";
      guest.attendingCount = isAttending ? (seats || guest.seatsAllocated || 1) : 0;
      guest.dietary = dietary || "";
      guest.message = message || "";
      saveData(data);
      return guest;
    },

    getMetrics: function () {
      const data = loadData();
      const totalInvitations = data.invitations.length;
      const activeInvitations = data.invitations.filter(i => i.isActive).length;
      const totalGuests = data.guests.length;
      const confirmedGuests = data.guests.filter(g => g.status === "confirmed").length;
      const totalAttendingSeats = data.guests
        .filter(g => g.status === "confirmed")
        .reduce((sum, g) => sum + (g.attendingCount || 0), 0);
      const checkedInGuests = data.guests.filter(g => g.checkIn).length;
      const checkedInSeats = data.guests
        .filter(g => g.checkIn)
        .reduce((sum, g) => sum + (g.attendingCount || 0), 0);

      const totalRevenue = data.invitations.reduce((sum, i) => sum + (i.price || 0), 0);
      const totalDeposits = data.invitations.reduce((sum, i) => sum + (i.depositPaid || 0), 0);
      const totalPending = data.invitations.reduce((sum, i) => sum + (i.balancePending || 0), 0);

      return {
        totalInvitations,
        activeInvitations,
        totalGuests,
        confirmedGuests,
        totalAttendingSeats,
        checkedInGuests,
        checkedInSeats,
        totalRevenue,
        totalDeposits,
        totalPending
      };
    }
  };

  // Helper para reloj regresivo
  function initCountdown(targetDateStr, containerSelectors) {
    const targetDate = new Date(targetDateStr).getTime();

    function update() {
      const now = new Date().getTime();
      const diff = targetDate - now;

      let d = 0, h = 0, m = 0, s = 0;
      if (diff > 0) {
        d = Math.floor(diff / (1000 * 60 * 60 * 24));
        h = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        m = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        s = Math.floor((diff % (1000 * 60)) / 1000);
      }

      containerSelectors.forEach(sel => {
        const el = typeof sel === "string" ? document.querySelector(sel) : sel;
        if (!el) return;
        el.innerHTML = `
          <div class="countdown-box-mini"><div class="val">${d}</div><div class="lbl">Días</div></div>
          <div class="countdown-box-mini"><div class="val">${String(h).padStart(2, '0')}</div><div class="lbl">Horas</div></div>
          <div class="countdown-box-mini"><div class="val">${String(m).padStart(2, '0')}</div><div class="lbl">Min</div></div>
          <div class="countdown-box-mini"><div class="val">${String(s).padStart(2, '0')}</div><div class="lbl">Seg</div></div>
        `;
      });
    }

    update();
    return setInterval(update, 1000);
  }

  // Toast feedback helper
  function showToast(message) {
    let toast = document.getElementById("ke-toast");
    if (!toast) {
      toast = document.createElement("div");
      toast.id = "ke-toast";
      toast.className = "toast-msg";
      document.body.appendChild(toast);
    }
    toast.textContent = message;
    toast.classList.add("show");
    setTimeout(() => {
      toast.classList.remove("show");
    }, 2800);
  }

  global.KE = {
    Store: Store,
    initCountdown: initCountdown,
    showToast: showToast
  };
})(window);
