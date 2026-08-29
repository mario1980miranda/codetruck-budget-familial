import React, { useState, useMemo } from "react";
import {
  PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer,
  BarChart, Bar, XAxis, YAxis, CartesianGrid,
} from "recharts";

const INK = "#1B2A3C";
const INK_MUTED = "#5B6B7A";
const RULE = "#DDD5C4";
const BG = "#FAF7F0";
const SURFACE = "#F3EEE3";
const GOLD = "#B08D57";

const CATEGORIE_COLORS = {
  ALIMENTATION: "#3F6249",
  PHARMACIE: "#7A3B46",
  LOGEMENT: "#4A5A70",
  TRANSPORT: "#B08D57",
  LOISIRS: "#8B5A8F",
  AUTRE: "#8A8378",
};

const TYPE_COLORS = {
  DEPENSE: "#7A3B46",
  REVENU: "#3F6249",
  EPARGNE: "#B08D57",
  TRANSFERT_INTERNE: "#8A8378",
};

const fmtMontant = (v) =>
  new Intl.NumberFormat("fr-CA", { style: "currency", currency: "CAD" }).format(v ?? 0);

const fmtDate = (v) => {
  if (!v) return "";
  const d = new Date(v + "T00:00:00");
  return new Intl.DateTimeFormat("fr-CA", { day: "2-digit", month: "short", year: "numeric" }).format(d);
};

function Card({ label, value, accent }) {
  return (
    <div style={{ background: SURFACE, borderRadius: 8, padding: "1rem 1.25rem", border: `1px solid ${RULE}` }}>
      <p style={{ fontSize: 12, color: INK_MUTED, margin: 0, textTransform: "uppercase", letterSpacing: "0.06em" }}>
        {label}
      </p>
      <p style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 22, fontWeight: 600, margin: "4px 0 0", color: accent || INK }}>
        {value}
      </p>
    </div>
  );
}

function OngletCategories() {
  const [texte, setTexte] = useState("");
  const [erreur, setErreur] = useState("");
  const [data, setData] = useState(null);
  const [chartType, setChartType] = useState("pie");

  const charger = () => {
    setErreur("");
    try {
      const parsed = JSON.parse(texte);
      if (!parsed.depensesParCategorie) {
        setErreur("Ce JSON ne ressemble pas à une réponse de /agregations (champ depensesParCategorie manquant).");
        return;
      }
      setData(parsed);
    } catch (e) {
      setErreur("JSON invalide - vérifie que tu as bien copié toute la réponse.");
    }
  };

  const chartData = useMemo(() => {
    if (!data) return [];
    return Object.entries(data.depensesParCategorie).map(([categorie, montant]) => ({
      categorie,
      montant,
      color: CATEGORIE_COLORS[categorie] || "#8A8378",
    }));
  }, [data]);

  return (
    <div>
      <p style={{ fontSize: 13, color: INK_MUTED, margin: "0 0 8px" }}>
        Colle ici la réponse de <code style={{ fontFamily: "'JetBrains Mono', monospace" }}>GET /agregations?debut=...&fin=...</code>
      </p>
      <textarea
        value={texte}
        onChange={(e) => setTexte(e.target.value)}
        placeholder='{"periodeDebut":"2026-07-01","periodeFin":"2026-08-31","totalRevenus":4638.46,...}'
        rows={5}
        style={{
          width: "100%", fontFamily: "'JetBrains Mono', monospace", fontSize: 12,
          padding: 10, borderRadius: 6, border: `1px solid ${RULE}`, background: "#fff",
          color: INK, resize: "vertical", boxSizing: "border-box",
        }}
      />
      <div style={{ display: "flex", gap: 8, marginTop: 10, alignItems: "center" }}>
        <button
          onClick={charger}
          style={{
            background: INK, color: "#fff", border: "none", borderRadius: 6,
            padding: "8px 16px", fontSize: 13, fontWeight: 600, cursor: "pointer",
          }}
        >
          Afficher
        </button>
        {erreur && <span style={{ fontSize: 13, color: "#7A3B46" }}>{erreur}</span>}
      </div>

      {data && (
        <div style={{ marginTop: 24 }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline", marginBottom: 16, flexWrap: "wrap", gap: 8 }}>
            <div
              style={{
                fontFamily: "'Fraunces', serif", fontSize: 13, color: INK, border: `1px solid ${GOLD}`,
                borderRadius: 4, padding: "4px 12px", letterSpacing: "0.04em", textTransform: "uppercase",
              }}
            >
              Période : {fmtDate(data.periodeDebut)} — {fmtDate(data.periodeFin)}
            </div>
            <div style={{ display: "flex", gap: 4 }}>
              <button
                onClick={() => setChartType("pie")}
                style={{
                  fontSize: 12, padding: "4px 10px", borderRadius: 4, cursor: "pointer",
                  border: `1px solid ${RULE}`, background: chartType === "pie" ? INK : "#fff",
                  color: chartType === "pie" ? "#fff" : INK_MUTED,
                }}
              >
                Camembert
              </button>
              <button
                onClick={() => setChartType("bar")}
                style={{
                  fontSize: 12, padding: "4px 10px", borderRadius: 4, cursor: "pointer",
                  border: `1px solid ${RULE}`, background: chartType === "bar" ? INK : "#fff",
                  color: chartType === "bar" ? "#fff" : INK_MUTED,
                }}
              >
                Barres
              </button>
            </div>
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(140px, 1fr))", gap: 12, marginBottom: 24 }}>
            <Card label="Revenus" value={fmtMontant(data.totalRevenus)} accent={TYPE_COLORS.REVENU} />
            <Card label="Dépenses" value={fmtMontant(data.totalDepenses)} accent={TYPE_COLORS.DEPENSE} />
            <Card label="Épargne" value={fmtMontant(data.totalEpargne)} accent={TYPE_COLORS.EPARGNE} />
            <Card
              label="Non alloué"
              value={fmtMontant((data.totalRevenus ?? 0) - (data.totalDepenses ?? 0) - (data.totalEpargne ?? 0))}
            />
          </div>

          <div style={{ height: 320 }}>
            <ResponsiveContainer width="100%" height="100%">
              {chartType === "pie" ? (
                <PieChart>
                  <Pie data={chartData} dataKey="montant" nameKey="categorie" cx="50%" cy="50%" outerRadius={110} label={(e) => `${e.categorie} ${fmtMontant(e.montant)}`}>
                    {chartData.map((entry, i) => (
                      <Cell key={i} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip formatter={(v) => fmtMontant(v)} />
                  <Legend />
                </PieChart>
              ) : (
                <BarChart data={chartData} layout="vertical" margin={{ left: 20 }}>
                  <CartesianGrid horizontal={false} stroke={RULE} />
                  <XAxis type="number" tickFormatter={fmtMontant} tick={{ fontSize: 11, fill: INK_MUTED }} />
                  <YAxis type="category" dataKey="categorie" tick={{ fontSize: 12, fill: INK }} width={100} />
                  <Tooltip formatter={(v) => fmtMontant(v)} />
                  <Bar dataKey="montant" radius={[0, 4, 4, 0]}>
                    {chartData.map((entry, i) => (
                      <Cell key={i} fill={entry.color} />
                    ))}
                  </Bar>
                </BarChart>
              )}
            </ResponsiveContainer>
          </div>
        </div>
      )}
    </div>
  );
}

function OngletTransactions() {
  const [texte, setTexte] = useState("");
  const [erreur, setErreur] = useState("");
  const [data, setData] = useState(null);
  const [tri, setTri] = useState({ colonne: "date", asc: true });

  const charger = () => {
    setErreur("");
    try {
      const parsed = JSON.parse(texte);
      if (!Array.isArray(parsed)) {
        setErreur("Ce JSON ne ressemble pas à une réponse de /transactions (un tableau était attendu).");
        return;
      }
      setData(parsed);
    } catch (e) {
      setErreur("JSON invalide - vérifie que tu as bien copié toute la réponse.");
    }
  };

  const trier = (colonne) => {
    setTri((prev) => (prev.colonne === colonne ? { colonne, asc: !prev.asc } : { colonne, asc: true }));
  };

  const lignesTriees = useMemo(() => {
    if (!data) return [];
    const copie = [...data];
    copie.sort((a, b) => {
      const va = a[tri.colonne] ?? "";
      const vb = b[tri.colonne] ?? "";
      if (typeof va === "number") return tri.asc ? va - vb : vb - va;
      return tri.asc ? String(va).localeCompare(String(vb)) : String(vb).localeCompare(String(va));
    });
    return copie;
  }, [data, tri]);

  const colonnes = [
    { key: "date", label: "Date" },
    { key: "description", label: "Description" },
    { key: "montant", label: "Montant" },
    { key: "typeTransaction", label: "Type" },
    { key: "categorie", label: "Catégorie" },
    { key: "compteNom", label: "Compte" },
    { key: "titulaire", label: "Titulaire" },
  ];

  return (
    <div>
      <p style={{ fontSize: 13, color: INK_MUTED, margin: "0 0 8px" }}>
        Colle ici la réponse de <code style={{ fontFamily: "'JetBrains Mono', monospace" }}>GET /transactions?debut=...&fin=...</code>
      </p>
      <textarea
        value={texte}
        onChange={(e) => setTexte(e.target.value)}
        placeholder='[{"date":"2026-07-19","description":"PAYPAL *STEAM GAMES",...}]'
        rows={5}
        style={{
          width: "100%", fontFamily: "'JetBrains Mono', monospace", fontSize: 12,
          padding: 10, borderRadius: 6, border: `1px solid ${RULE}`, background: "#fff",
          color: INK, resize: "vertical", boxSizing: "border-box",
        }}
      />
      <div style={{ display: "flex", gap: 8, marginTop: 10, alignItems: "center" }}>
        <button
          onClick={charger}
          style={{
            background: INK, color: "#fff", border: "none", borderRadius: 6,
            padding: "8px 16px", fontSize: 13, fontWeight: 600, cursor: "pointer",
          }}
        >
          Afficher
        </button>
        {erreur && <span style={{ fontSize: 13, color: "#7A3B46" }}>{erreur}</span>}
        {data && <span style={{ fontSize: 13, color: INK_MUTED }}>{data.length} transaction{data.length > 1 ? "s" : ""}</span>}
      </div>

      {data && (
        <div style={{ marginTop: 20, overflowX: "auto" }}>
          <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 13 }}>
            <thead>
              <tr>
                {colonnes.map((c) => (
                  <th
                    key={c.key}
                    onClick={() => trier(c.key)}
                    style={{
                      textAlign: c.key === "montant" ? "right" : "left", padding: "6px 10px",
                      borderBottom: `2px solid ${INK}`, cursor: "pointer", userSelect: "none",
                      fontFamily: "'Fraunces', serif", fontWeight: 600, color: INK, fontSize: 12.5,
                      textTransform: "uppercase", letterSpacing: "0.03em", whiteSpace: "nowrap",
                    }}
                  >
                    {c.label}{tri.colonne === c.key ? (tri.asc ? " ▲" : " ▼") : ""}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {lignesTriees.map((t, i) => (
                <tr key={i} style={{ borderBottom: `1px solid ${RULE}` }}>
                  <td style={{ padding: "6px 10px", fontFamily: "'JetBrains Mono', monospace", color: INK_MUTED, whiteSpace: "nowrap" }}>
                    {fmtDate(t.date)}
                  </td>
                  <td style={{ padding: "6px 10px", color: INK }}>{t.description}</td>
                  <td style={{ padding: "6px 10px", textAlign: "right", fontFamily: "'JetBrains Mono', monospace", color: INK, fontWeight: 500 }}>
                    {fmtMontant(t.montant)}
                  </td>
                  <td style={{ padding: "6px 10px" }}>
                    <span
                      style={{
                        fontSize: 11, padding: "2px 8px", borderRadius: 10, whiteSpace: "nowrap",
                        background: `${TYPE_COLORS[t.typeTransaction] || "#8A8378"}22`,
                        color: TYPE_COLORS[t.typeTransaction] || "#8A8378",
                      }}
                    >
                      {t.typeTransaction}
                    </span>
                  </td>
                  <td style={{ padding: "6px 10px" }}>
                    {t.categorie && (
                      <span
                        style={{
                          fontSize: 11, padding: "2px 8px", borderRadius: 10, whiteSpace: "nowrap",
                          background: `${CATEGORIE_COLORS[t.categorie] || "#8A8378"}22`,
                          color: CATEGORIE_COLORS[t.categorie] || "#8A8378",
                        }}
                      >
                        {t.categorie}
                      </span>
                    )}
                  </td>
                  <td style={{ padding: "6px 10px", color: INK_MUTED, whiteSpace: "nowrap" }}>{t.compteNom}</td>
                  <td style={{ padding: "6px 10px", color: INK_MUTED, whiteSpace: "nowrap" }}>{t.titulaire}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default function TableauDeBordBudget() {
  const [onglet, setOnglet] = useState("categories");

  return (
    <div style={{ background: BG, minHeight: "100%", padding: "24px 28px", fontFamily: "'Inter', sans-serif", boxSizing: "border-box" }}>
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Fraunces:wght@500;600&family=Inter:wght@400;500;600&family=JetBrains+Mono:wght@400;500;600&display=swap');
      `}</style>

      <h1 style={{ fontFamily: "'Fraunces', serif", fontSize: 24, fontWeight: 600, color: INK, margin: "0 0 4px" }}>
        Budget familial
      </h1>
      <p style={{ fontSize: 13, color: INK_MUTED, margin: "0 0 20px" }}>
        Colle les réponses JSON de ton API — rien n'est envoyé nulle part, tout reste dans ton navigateur.
      </p>

      <div style={{ display: "flex", gap: 2, marginBottom: 20, borderBottom: `1px solid ${RULE}` }}>
        {[
          { id: "categories", label: "Dépenses par catégorie" },
          { id: "transactions", label: "Liste des transactions" },
        ].map((t) => (
          <button
            key={t.id}
            onClick={() => setOnglet(t.id)}
            style={{
              padding: "8px 16px", fontSize: 13, fontWeight: 500, cursor: "pointer",
              background: "transparent", border: "none",
              borderBottom: onglet === t.id ? `2px solid ${INK}` : "2px solid transparent",
              color: onglet === t.id ? INK : INK_MUTED, marginBottom: -1,
            }}
          >
            {t.label}
          </button>
        ))}
      </div>

      {onglet === "categories" ? <OngletCategories /> : <OngletTransactions />}
    </div>
  );
}
