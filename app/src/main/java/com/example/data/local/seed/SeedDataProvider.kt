package com.example.data.local.seed

import com.example.data.local.entity.*

object SeedDataProvider {

    fun getDefaultProfile(): MRProfileEntity {
        return MRProfileEntity(
            empId = "CO-MR-8492",
            name = "Aman Chhabra",
            phone = "+91 98765 43210",
            email = "aman.chhabra@careosis.com",
            territory = "North Delhi & Rohini Central",
            managerName = "Rajesh Verma (Area Manager)",
            joiningDate = "15 Jan 2025",
            designation = "Senior Medical Representative",
            level = "Expert MR",
            trainingProgressPercent = 78,
            monthlyTarget = 200000.0,
            monthlySales = 156800.0,
            currentIncentive = 8450.0,
            isCheckedInToday = true,
            checkInTime = "08:45 AM",
            completedVisitsToday = 12,
            targetVisitsToday = 15
        )
    }

    fun getInitialProducts(): List<ProductEntity> {
        return listOf(
            ProductEntity(
                id = "PROD-001",
                name = "Booster",
                brand = "CareOsis",
                category = "Nutraceutical",
                mrp = 320.0,
                retailerRate = 224.0,
                packaging = "10 x 1 x 10 Effervescent Tablets (Orange Flavour)",
                composition = "L-Carnitine L-Tartrate 500mg + CoQ10 100mg + Lycopene 10% 5000mcg + Zinc Sulphate 22.5mg + Elemental Selenium 40mcg",
                indications = "Chronic Fatigue Syndrome, Post-Viral Asthenia, Statin-Induced Myopathy, Male Sub-Fertility, Cardiac Vitality",
                keyBenefits = "Rapid cellular ATP generation, fast effervescent absorption within 15 minutes, zero gastric irritation, delightful citrus palatability",
                mechanismOfAction = "L-Carnitine shuttles long-chain fatty acids into mitochondria for beta-oxidation, while CoQ10 optimizes Electron Transport Chain Complex I & III to boost cellular ATP synthesis by 3.2x.",
                dosage = "1 effervescent tablet dissolved in 200ml fresh water once daily after breakfast",
                mrPitch = "Doctor, when patients complain of persistent post-illness lethargy despite normal labs, Booster delivers instant bioavailable mitochondrial fuel without gastric heaviness.",
                importantTalkingPoints = "100% water-soluble CoQ10; Higher bioavailability than oily capsules; Sugar-free, safe for diabetics",
                clinicalEvidence = "Published trial of 120 patients demonstrated a 68% reduction in fatigue scores within 14 days of CoQ10 + L-Carnitine effervescent supplementation.",
                competitorInfo = "vs Standard Softgels: 3x faster absorption, 0% oily aftertaste, 15% better price-per-dose value.",
                videoTitle = "Booster: Mitochondrial Bioenergetics in Practice",
                videoDuration = "3m 45s",
                isFocusProduct = true
            ),
            ProductEntity(
                id = "PROD-002",
                name = "Calci Fizz",
                brand = "CareOsis",
                category = "Nutraceutical / Ortho",
                mrp = 280.0,
                retailerRate = 196.0,
                packaging = "20 Effervescent Tablets in Moisture-Barrier Tube",
                composition = "Calcium Carbonate (from Organic Coral Source) 1000mg + Vitamin D3 1000 IU + Magnesium Carbonate 100mg + Vitamin K2-7 55mcg",
                indications = "Post-Menopausal Osteopenia, Fracture Healing, Pregnancy & Lactation Calcium Demand, Senile Osteoporosis",
                keyBenefits = "Zero constipation or bloating, Vitamin K2-7 ensures calcium deposits into bones rather than arterial walls, 100% elemental absorption",
                mechanismOfAction = "Ionized calcium in effervescent solution bypasses stomach acid dependency. Vitamin K2-7 activates osteocalcin to bind ionized calcium firmly into hydroxyapatite bone matrix.",
                dosage = "1 tablet in 150ml water daily after lunch",
                mrPitch = "Doctor, unlike conventional chalky calcium tablets that cause severe constipation, Calci Fizz provides pleasant effervescent organic calcium with K2-7 protection.",
                importantTalkingPoints = "Organic coral calcium base; 99% patient compliance; Strawberry-Lemon fizz",
                clinicalEvidence = "Bone Mineral Density (BMD) study showed 4.2% higher T-score improvement over 6 months vs plain calcium carbonate tablets.",
                competitorInfo = "vs Shelcal / Gemcal: Superior GI tolerance, no kidney stone risk due to K2-7 routing.",
                videoTitle = "Calci Fizz: Calcium Routing with K2-7",
                videoDuration = "4m 10s",
                isFocusProduct = true
            ),
            ProductEntity(
                id = "PROD-003",
                name = "Metabo 3X",
                brand = "CareOsis",
                category = "Nutraceutical / Diabetology",
                mrp = 450.0,
                retailerRate = 315.0,
                packaging = "30 Sustained Release Bilayer Tablets",
                composition = "Berberine HCl 500mg + Chromium Picolinate 400mcg + Alpha Lipoic Acid 200mg + Cinnamon Extract 150mg",
                indications = "Type-2 Diabetes Adjuvant, Insulin Resistance, Metabolic Syndrome, Dyslipidemia, PCOS weight management",
                keyBenefits = "Reduces HbA1c by activating AMPK enzyme, decreases postprandial glycemic spikes, curbs carbohydrate cravings",
                mechanismOfAction = "Berberine phosphorylates and activates AMPK (AMP-activated protein kinase), stimulating GLUT-4 translocation in muscle cells and suppressing hepatic gluconeogenesis.",
                dosage = "1 tablet twice daily 15 minutes before meals",
                mrPitch = "Doctor, for borderline diabetic and PCOS patients struggling with insulin resistance, Metabo 3X offers natural AMPK pathway activation synergistic with standard therapy.",
                importantTalkingPoints = "Clinically validated Berberine phytosome; dual bilayer release; cardioprotective lipid modulation",
                clinicalEvidence = "Double-blind study: Mean HbA1c reduction of 0.8% and fasting triglycerides reduction of 22% over 12 weeks.",
                competitorInfo = "vs Plain Metformin monotherapy: Zero lactic acidosis risk, excellent auxiliary lipid lowering.",
                videoTitle = "Metabo 3X: Mastering the AMPK Metabolic Switch",
                videoDuration = "5m 20s",
                isFocusProduct = true
            ),
            ProductEntity(
                id = "PROD-004",
                name = "Nutri Digest",
                brand = "CareOsis",
                category = "Gastroenterology",
                mrp = 220.0,
                retailerRate = 154.0,
                packaging = "10 x 10 Enteric Coated Capsules",
                composition = "Fungal Diastase (1:1200) 50mg + Pepsin (1:3000) 10mg + Lipase 5000 IU + Amylase 10000 IU + Protease 25000 IU + Bacillus Coagulans 2 Billion Spores",
                indications = "Functional Dyspepsia, Post-Prandial Bloating, Heavy Meal Indigestion, Pancreatic Enzyme Insufficiency",
                keyBenefits = "Broad-spectrum digestive breakdown of proteins, carbs, and fats + spore-form probiotic for gut microbiome restoration",
                mechanismOfAction = "Multi-enzymes hydrolyze complex starches, lipids, and peptide bonds under enteric micro-environment, while B. coagulans spores colonize lower bowel to suppress putrefactive gas formation.",
                dosage = "1 capsule immediately after major meals",
                mrPitch = "Doctor, Nutri Digest combines pancreatic enzymes with spore probiotics, eliminating the post-meal heaviness and gas immediately.",
                importantTalkingPoints = "Enteric protected; active across broad pH 3.0 to 8.5; stable room temperature storage",
                clinicalEvidence = "92% relief in epigastric fullness and early satiety reported within 3 days of therapy.",
                competitorInfo = "vs Aristozyme / Digeplex: Added high-potency Lipase + Spore Probiotics for long-term gut balance.",
                videoTitle = "Nutri Digest: Complete Digestive Spectrum",
                videoDuration = "3m 15s",
                isFocusProduct = false
            ),
            ProductEntity(
                id = "PROD-005",
                name = "Maxvit 7G",
                brand = "CareOsis",
                category = "Nutraceutical",
                mrp = 390.0,
                retailerRate = 273.0,
                packaging = "3 x 10 Softgel Capsules in Alu-Alu Blister",
                composition = "Ginseng + Ginkgo Biloba + Green Tea Extract + Grape Seed Extract + Garlic Extract + Guggul + Ginger + 24 Essential Multivitamins & Minerals",
                indications = "General Debility, Cardiovascular Support, Cognitive Alertness, Geriatric Vitality, Stress Management",
                keyBenefits = "Comprehensive 7-G botanical adaptogen complex + full RDA micronutrients for all-day stamina and immunity",
                mechanismOfAction = "Ginsenosides and Ginkgo flavonoids enhance cerebral microcirculation and modulate cortisol, reducing oxidative stress on vascular endothelium.",
                dosage = "1 softgel daily after breakfast",
                mrPitch = "Doctor, Maxvit 7G combines 7 power green botanicals with therapeutic micronutrients for executive stress and post-viral recovery.",
                importantTalkingPoints = "Premium softgel formulation; deodorized garlic extract; no regurgitation",
                clinicalEvidence = "Demonstrated 45% increase in physical endurance and cognitive recall index in working professionals.",
                competitorInfo = "vs Becadexamin / Revital: 7G extract power, superior antioxidant capacity index (ORAC).",
                videoTitle = "Maxvit 7G: The 7-Botanical Shield",
                videoDuration = "4m 00s",
                isFocusProduct = true
            ),
            ProductEntity(
                id = "PROD-006",
                name = "Ferosis",
                brand = "CareOsis",
                category = "Hematology / Gynecology",
                mrp = 195.0,
                retailerRate = 136.5,
                packaging = "10 x 10 Film Coated Tablets",
                composition = "Ferrous Ascorbate equivalent to Elemental Iron 100mg + Folic Acid 1.5mg + Zinc Sulphate Monohydrate 61.8mg",
                indications = "Iron Deficiency Anemia, Nutritional Anemia in Pregnancy & Lactation, Menorrhagia blood loss",
                keyBenefits = "Fastest Hb rise (1.5 - 2.0 g/dL per month), reduced gastric irritation compared to ferrous sulphate, optimal zinc inclusion",
                mechanismOfAction = "Ascorbate maintains iron in divalent (Fe2+) soluble state in duodenal lumen, facilitating active DMT-1 transporter uptake.",
                dosage = "1 tablet daily after food",
                mrPitch = "Doctor, Ferosis provides Ferrous Ascorbate which guarantees 3x higher absorption with minimal constipation for expectant mothers.",
                importantTalkingPoints = "Rapid reticulocyte response; tasteless smooth coating; zero teeth staining",
                clinicalEvidence = "Hb elevation from 8.4 g/dL to 10.8 g/dL in 45 days in second trimester pregnant cohorts.",
                competitorInfo = "vs Orofer XT / Autrin: Gentle on the stomach with superior organic ascorbate chelation.",
                videoTitle = "Ferosis: Elevating Hemoglobin Safely",
                videoDuration = "3m 30s",
                isFocusProduct = false
            ),
            ProductEntity(
                id = "PROD-007",
                name = "Nervicobal",
                brand = "CareOsis",
                category = "Neurology / Ortho",
                mrp = 340.0,
                retailerRate = 238.0,
                packaging = "10 x 10 Capsules in Monocarton",
                composition = "Methylcobalamin 1500mcg + Alpha Lipoic Acid 100mg + Pyridoxine HCl 3mg + Folic Acid 1.5mg + Benfotiamine 50mg",
                indications = "Diabetic Peripheral Neuropathy, Sciatica, Cervical Spondylosis tingling & numbness, Alcoholic Neuropathy",
                keyBenefits = "Regenerates injured myelin sheath, relieves burning feet sensation, lipid-soluble Benfotiamine penetrates nerve tissue 5x better",
                mechanismOfAction = "Methylcobalamin promotes transmethylation in myelin basic protein synthesis, while Alpha Lipoic Acid neutralizes oxidative neuro-vascular free radicals.",
                dosage = "1 capsule daily after dinner",
                mrPitch = "Doctor, for patients enduring burning feet and diabetic nerve pain, Nervicobal restores the myelin sheath and terminates the tingling.",
                importantTalkingPoints = "Includes Benfotiamine for deep nerve penetration; ALA relieves painful paresthesia",
                clinicalEvidence = "78% reduction in neuropathic pain visual analogue scale (VAS) score after 4 weeks.",
                competitorInfo = "vs Neurobion Forte / Reclimet: Benfotiamine + ALA combination delivers far superior nerve regeneration.",
                videoTitle = "Nervicobal: Myelin Restoration Mechanism",
                videoDuration = "4m 15s",
                isFocusProduct = true
            ),
            ProductEntity(
                id = "PROD-008",
                name = "Cefosis",
                brand = "CareOsis",
                category = "Antibiotic",
                mrp = 210.0,
                retailerRate = 147.0,
                packaging = "10 x 10 Dispersible Tablets in Alu-Alu",
                composition = "Cefixime Trihydrate IP eq. to Anhydrous Cefixime 200mg (Dispersible)",
                indications = "Typhoid Fever, Community Acquired Pneumonia, Acute Bronchitis, UTI, Uncomplicated Gonorrhea",
                keyBenefits = "Potent 3rd gen cephalosporin, rapid dispersal in 30 seconds, high bioavailability against beta-lactamase producers",
                mechanismOfAction = "Binds to penicillin-binding proteins (PBPs), inhibiting bacterial cell wall peptidoglycan synthesis causing bacterial lysis.",
                dosage = "1 tablet twice daily for 5-7 days",
                mrPitch = "Doctor, Cefosis 200 DT gives swift microbial eradication in Typhoid and respiratory infections with rapid dispersion.",
                importantTalkingPoints = "Fast dispersion in water; stable in gastric acid; broad coverage against Gram-positive & Gram-negative pathogens",
                clinicalEvidence = "95% clinical cure rate in enteric fever with zero defervescence delay.",
                competitorInfo = "vs Zifi / Taxim-O: Identical bioavailability with superior tablet dissolution time.",
                videoTitle = "Cefosis: 3rd Gen Cephalosporin Precision",
                videoDuration = "3m 00s",
                isFocusProduct = false
            ),
            ProductEntity(
                id = "PROD-009",
                name = "Cefodosis",
                brand = "CareOsis",
                category = "Antibiotic",
                mrp = 295.0,
                retailerRate = 206.5,
                packaging = "10 x 10 Film Coated Tablets",
                composition = "Cefpodoxime Proxetil IP eq. to Cefpodoxime 200mg",
                indications = "Acute Otitis Media, Sinusitis, Pharyngitis / Tonsillitis, Lower Respiratory Tract Infections",
                keyBenefits = "Broad antimicrobial spectrum, exceptional tissue penetration in lung parenchyma and middle ear mucosa",
                mechanismOfAction = "Pro-drug de-esterified in intestinal mucosa to active cefpodoxime, blocking transpeptidase cross-linking of peptidoglycan.",
                dosage = "1 tablet twice daily with food",
                mrPitch = "Doctor, for stubborn ENT and upper respiratory tract infections, Cefodosis 200 delivers exceptional tissue concentrations.",
                importantTalkingPoints = "High respiratory tract bioavailability; excellent safety profile across adult age groups",
                clinicalEvidence = "Eradicated 94% of S. pneumoniae and H. influenzae in clinical respiratory trials.",
                competitorInfo = "vs Gudcef / Monocef-O: Premium blister stability and assured dissolution profile.",
                videoTitle = "Cefodosis: Upper Respiratory Masterclass",
                videoDuration = "3m 20s",
                isFocusProduct = false
            ),
            ProductEntity(
                id = "PROD-010",
                name = "Amosis",
                brand = "CareOsis",
                category = "Antibiotic",
                mrp = 240.0,
                retailerRate = 168.0,
                packaging = "10 x 1 x 6 Tablets with Desiccant Pouch",
                composition = "Amoxicillin Trihydrate IP 500mg + Potassium Clavulanate Diluted IP 125mg",
                indications = "Dental Infections, Skin & Soft Tissue Infections, Animal Bites, Recurrent Sinusitis, Post-Surgical Prophylaxis",
                keyBenefits = "Clavulanic acid irreversibly inactivates bacterial beta-lactamase enzymes, restoring amoxicillin power",
                mechanismOfAction = "Amoxicillin acts as a bactericidal agent against susceptible organisms; clavulanate acts as a suicide inhibitor of beta-lactamases.",
                dosage = "1 tablet twice daily at the start of a light meal",
                mrPitch = "Doctor, Amosis 625 overcomes bacterial beta-lactamase resistance with zero moisture degradation in our desiccant Alu-Alu pack.",
                importantTalkingPoints = "Moisture-barrier desiccant strip prevents clavulanic acid hydrolysis; consistent potency",
                clinicalEvidence = "96% success in complicated skin, soft tissue and odontogenic infections.",
                competitorInfo = "vs Augmentin / Moxikind-CV: Advanced moisture-sealed packing eliminates degradation discoloration.",
                videoTitle = "Amosis: Overcoming Resistance",
                videoDuration = "3m 40s",
                isFocusProduct = false
            ),
            ProductEntity(
                id = "PROD-011",
                name = "Lizosis",
                brand = "CareOsis",
                category = "Antibiotic / Critical Care",
                mrp = 490.0,
                retailerRate = 343.0,
                packaging = "10 x 1 x 10 Tablets in Monocarton",
                composition = "Linezolid IP 600mg",
                indications = "MRSA (Methicillin-Resistant S. aureus), VRE infections, Hospital-Acquired Pneumonia, Complicated Diabetic Foot Ulcers",
                keyBenefits = "100% oral bioavailability (matches IV dosing), potent against multidrug-resistant Gram-positive pathogens",
                mechanismOfAction = "Inhibits bacterial protein synthesis at initiation phase by binding to the 23S portion of the 50S ribosomal subunit.",
                dosage = "1 tablet twice daily for 10-14 days",
                mrPitch = "Doctor, when managing refractory MRSA or diabetic foot infections, Lizosis 600 offers 100% oral bioavailability matching IV therapy.",
                importantTalkingPoints = "Zero cross-resistance with other antibiotic classes; seamless IV-to-oral step-down therapy",
                clinicalEvidence = "91% cure rate in documented MRSA skin and soft-tissue infections.",
                competitorInfo = "vs Linid / Lizomac: Assured pharmaceutical grade purity with minimal GI side-effects.",
                videoTitle = "Lizosis: The MRSA Guardian",
                videoDuration = "4m 10s",
                isFocusProduct = false
            ),
            ProductEntity(
                id = "PROD-012",
                name = "Farocare",
                brand = "CareOsis",
                category = "Antibiotic / Super-Specialty",
                mrp = 850.0,
                retailerRate = 595.0,
                packaging = "10 x 1 x 6 Extended Release Tablets",
                composition = "Faropenem Sodium IP 200mg (Extended Release)",
                indications = "Complicated UTI, ESBL-producing pathogen infections, Refractory Respiratory Infections",
                keyBenefits = "Only oral penem antibiotic, resilient against ESBL and AmpC beta-lactamases, ER formulation provides sustained bactericidal coverage",
                mechanismOfAction = "Possesses ultra-high affinity for high molecular weight PBPs (PBP-2 and PBP-1a/b), withstanding class A, C, and D beta-lactamases.",
                dosage = "1 tablet twice daily with or without food",
                mrPitch = "Doctor, for resistant ESBL urinary and respiratory infections where oral options are exhausted, Farocare 200 ER avoids hospitalization.",
                importantTalkingPoints = "Oral carbapenem surrogate; Extended Release prevents plasma concentration dips; saves hospital bed days",
                clinicalEvidence = "89% microbiological eradication in recurrent multi-drug resistant UTIs.",
                competitorInfo = "vs Farobact / Duonem: Advanced ER matrix delivers consistent 12-hour MIC threshold coverage.",
                videoTitle = "Farocare: Oral Penem Advantage",
                videoDuration = "4m 45s",
                isFocusProduct = true
            ),
            ProductEntity(
                id = "PROD-013",
                name = "Levosis LC",
                brand = "CareOsis",
                category = "Respiratory / Allergy",
                mrp = 160.0,
                retailerRate = 112.0,
                packaging = "10 x 10 Tablets in Alu-Alu",
                composition = "Levocetirizine Dihydrochloride IP 5mg + Montelukast Sodium IP eq. to Montelukast 10mg",
                indications = "Allergic Rhinitis, Seasonal Allergies, Chronic Idiopathic Urticaria, Mild Allergic Asthma",
                keyBenefits = "Dual histamine and leukotriene receptor blockade; prevents nocturnal congestion; non-sedating",
                mechanismOfAction = "Levocetirizine blocks peripheral H1 receptors while Montelukast antagonizes cysteinyl leukotriene CysLT1 receptors on airway smooth muscles.",
                dosage = "1 tablet at bedtime",
                mrPitch = "Doctor, Levosis LC tackles both early and late phase allergic cascades, providing 24-hour congestion relief without morning grogginess.",
                importantTalkingPoints = "Bedtime single-dose convenience; rapid 1-hour onset of action; high pediatric and adult safety",
                clinicalEvidence = "86% improvement in Total Nasal Symptom Score (TNSS) compared to baseline.",
                competitorInfo = "vs Montair LC / Telekast-L: Precise dissolution kinetic profile ensuring non-sedative mornings.",
                videoTitle = "Levosis LC: Dual Allergic Cascade Blockade",
                videoDuration = "3m 10s",
                isFocusProduct = false
            ),
            ProductEntity(
                id = "PROD-014",
                name = "Pandocare DSR",
                brand = "CareOsis",
                category = "Gastroenterology",
                mrp = 175.0,
                retailerRate = 122.5,
                packaging = "10 x 10 Capsules in Alu-Alu",
                composition = "Pantoprazole Sodium IP eq. to Pantoprazole 40mg (Enteric Coated) + Domperidone IP 30mg (Sustained Release)",
                indications = "GERD, Reflux Esophagitis, Non-Ulcer Dyspepsia, NSAID-Induced Gastritis, Morning Nausea with Acidity",
                keyBenefits = "24-hour sustained gastric acid suppression + prokinetic gastric motility acceleration, zero nocturnal reflux breakthrough",
                mechanismOfAction = "Pantoprazole irreversibly blocks H+/K+ ATPase enzyme; Domperidone blocks peripheral dopamine D2 receptors in upper GI tract.",
                dosage = "1 capsule daily 30 minutes before breakfast",
                mrPitch = "Doctor, Pandocare DSR ensures full 24-hour acid suppression and accelerated gastric emptying for troublesome GERD patients.",
                importantTalkingPoints = "Uniform pellet technology; no acid rebound; ideal co-prescription with pain killers",
                clinicalEvidence = "Complete esophageal mucosal healing observed in 93% of reflux esophagitis cases at 4 weeks.",
                competitorInfo = "vs Pan-D / Pantocid DSR: Uniform multi-unit pellet system guarantees predictable prokinetic release.",
                videoTitle = "Pandocare DSR: 24-Hour Reflux Shield",
                videoDuration = "3m 25s",
                isFocusProduct = false
            ),
            ProductEntity(
                id = "PROD-015",
                name = "Etrosis",
                brand = "CareOsis",
                category = "Orthopedics / Pain Management",
                mrp = 210.0,
                retailerRate = 147.0,
                packaging = "10 x 10 Film Coated Tablets",
                composition = "Etoricoxib IP 90mg / 120mg",
                indications = "Osteoarthritis flare-ups, Rheumatoid Arthritis, Acute Gouty Arthritis, Post-Operative Dental Pain",
                keyBenefits = "Selective COX-2 inhibitor with 106-fold selectivity, single daily dose, rapid pain relief within 24 minutes",
                mechanismOfAction = "Selectively suppresses Cyclooxygenase-2 without affecting gastroprotective COX-1 prostaglandins.",
                dosage = "1 tablet once daily after food",
                mrPitch = "Doctor, for severe joint pain and acute gout attacks, Etrosis provides fast 24-minute analgesic onset with GI safety.",
                importantTalkingPoints = "Ultra-fast oral dissolution; 24-hour half-life; lowest incidence of GI bleeding among NSAIDs",
                clinicalEvidence = "Equivalent pain relief to maximum-dose indomethacin in acute gout with 60% fewer adverse GI events.",
                competitorInfo = "vs Nucoxia / Arcoxia: High purity crystal form ensuring consistent plasma Cmax.",
                videoTitle = "Etrosis: Selective COX-2 Precision",
                videoDuration = "3m 30s",
                isFocusProduct = false
            ),
            ProductEntity(
                id = "PROD-016",
                name = "AP Care SP",
                brand = "CareOsis",
                category = "Pain & Inflammation",
                mrp = 135.0,
                retailerRate = 94.5,
                packaging = "10 x 10 Tablets in Alu-Alu",
                composition = "Aceclofenac IP 100mg + Paracetamol IP 325mg + Serratiopeptidase IP 15mg",
                indications = "Post-Traumatic Edema, Dental Surgery Inflammation, Sports Injuries, Spondylitis, Post-Operative Wound Swelling",
                keyBenefits = "Triple action: Aceclofenac stops pain, Paracetamol resets thermal setpoint, Serratiopeptidase clears micro-thrombi and necrotic edema",
                mechanismOfAction = "Serratiopeptidase hydrolyzes inflammatory bradykinin and histamine exudates, facilitating greater antibiotic & NSAID penetration.",
                dosage = "1 tablet twice daily after food",
                mrPitch = "Doctor, AP Care SP accelerates surgical wound and trauma healing by dissolving inflammatory exudates alongside potent pain relief.",
                importantTalkingPoints = "Enteric protected serratiopeptidase ensures full proteolytic enzyme activity in intestinal lumen",
                clinicalEvidence = "50% faster resolution of dental and post-traumatic swelling vs standard NSAID monotherapy.",
                competitorInfo = "vs Zerodol-SP / Signoflam: Fully enteric-coated enzyme cores prevent stomach degradation.",
                videoTitle = "AP Care SP: The Triple-Action Anti-Inflammatory",
                videoDuration = "3m 15s",
                isFocusProduct = false
            ),
            ProductEntity(
                id = "PROD-017",
                name = "Drotacare",
                brand = "CareOsis",
                category = "Gastroenterology / Gynecology",
                mrp = 145.0,
                retailerRate = 101.5,
                packaging = "10 x 10 Tablets in Blister Pack",
                composition = "Drotaverine Hydrochloride IP 80mg + Mefenamic Acid IP 250mg",
                indications = "Spasmodic Dysmenorrhea, Uterine Cramps, Biliary Colic, Renal Colic Spasms, Irritable Bowel Spasm",
                keyBenefits = "Direct smooth muscle phosphodiesterase-IV (PDE-4) inhibition + prostaglandin synthesis suppression, zero anticholinergic dry mouth",
                mechanismOfAction = "Drotaverine inhibits PDE-4, increasing intracellular cAMP to relax smooth muscle; Mefenamic acid suppresses uterine prostaglandins.",
                dosage = "1 tablet 2-3 times daily during acute spasmodic episode",
                mrPitch = "Doctor, for excruciating spasmodic dysmenorrhea or renal colic, Drotacare stops spasms instantly without anticholinergic side-effects.",
                importantTalkingPoints = "Zero dry mouth, blurred vision, or tachycardia (unlike dicyclomine); selective for smooth muscle",
                clinicalEvidence = "88% relief of dysmenorrhea pain within 30 minutes of administration.",
                competitorInfo = "vs Drotin-M / Meftal-Spas: Non-anticholinergic mechanism ensures superior safety in elderly and cardiac patients.",
                videoTitle = "Drotacare: Antispasmodic Mastery",
                videoDuration = "3m 05s",
                isFocusProduct = false
            ),
            ProductEntity(
                id = "PROD-018",
                name = "Udiosi",
                brand = "CareOsis",
                category = "Hepatology / Gastroenterology",
                mrp = 620.0,
                retailerRate = 434.0,
                packaging = "10 x 10 Tablets in Monocarton",
                composition = "Ursodeoxycholic Acid IP 300mg",
                indications = "Non-Alcoholic Fatty Liver Disease (NAFLD/NASH), Primary Biliary Cholangitis, Radiolucent Gallstones Dissolution",
                keyBenefits = "Hepatoprotective bile acid, displaces toxic hydrophobic bile salts, stabilizes hepatocyte membranes and reduces liver enzymes",
                mechanismOfAction = "Enriches hydrophilic bile acid pool, stimulates canalicular bile secretion, protects cholangiocytes against bile salt apoptosis.",
                dosage = "1 tablet twice daily with milk or meals",
                mrPitch = "Doctor, for the rising NAFLD and NASH patient population, Udiosi 300 normalizes ALT/AST and protects hepatocytes from lipotoxicity.",
                importantTalkingPoints = "High purity UDCA; proven long-term safety; reduces liver fibrosis biomarkers",
                clinicalEvidence = "Statistically significant 42% decrease in serum AST/ALT levels over 6 months in NAFLD cohorts.",
                competitorInfo = "vs Udiliv / Ursocol: High crystalline bioavailability ensuring maximum micellar solubilization.",
                videoTitle = "Udiosi: Hepatoprotection & Bile Chemistry",
                videoDuration = "4m 20s",
                isFocusProduct = true
            ),
            ProductEntity(
                id = "PROD-019",
                name = "Pandocare Inj",
                brand = "CareOsis",
                category = "Critical Care / Hospital",
                mrp = 65.0,
                retailerRate = 45.5,
                packaging = "10ml Sterile Vial with Sterile Water for Injection",
                composition = "Sterile Pantoprazole Sodium for Injection IP eq. to Pantoprazole 40mg (Lyophilized)",
                indications = "Acute Upper GI Bleeding, Stress Ulcer Prophylaxis in ICU, Severe Acute GERD where oral route is unavailable",
                keyBenefits = "Instant IV acid suppression within 15 minutes, sustained gastric pH > 6.0 to promote platelet clot stabilization",
                mechanismOfAction = "Direct IV delivery irreversibly blocks active proton pumps in gastric parietal cells, maintaining intragastric pH > 6.0.",
                dosage = "40mg slow IV injection over 2 to 5 minutes once or twice daily",
                mrPitch = "Doctor, for ICU stress ulcer prevention and acute GI bleeds, Pandocare Inj provides instant pH elevation with smooth reconstitution.",
                importantTalkingPoints = "High-tech lyophilized cake dissolves in < 5 seconds; crystal-clear solution; zero particulate matter",
                clinicalEvidence = "Maintains gastric pH above 6.0 in 98% of ICU patients, significantly decreasing re-bleeding incidence.",
                competitorInfo = "vs Pan IV / Pantocid IV: Instant reconstitution with zero foam formation.",
                videoTitle = "Pandocare Inj: Emergency Acid Control",
                videoDuration = "2m 50s",
                isFocusProduct = false
            )
        )
    }

    fun getInitialTrainingProgress(): List<TrainingProgressEntity> {
        return listOf(
            TrainingProgressEntity("PROD-001", "Booster", "Nutraceutical", dossierRead = true, videoWatched = true, quizScore = 90, isCompleted = true, completionPercentage = 95),
            TrainingProgressEntity("PROD-002", "Calci Fizz", "Nutraceutical / Ortho", dossierRead = true, videoWatched = true, quizScore = 80, isCompleted = true, completionPercentage = 90),
            TrainingProgressEntity("PROD-003", "Metabo 3X", "Nutraceutical / Diabetology", dossierRead = true, videoWatched = true, quizScore = 100, isCompleted = true, completionPercentage = 100),
            TrainingProgressEntity("PROD-004", "Nutri Digest", "Gastroenterology", dossierRead = true, videoWatched = false, quizScore = 75, isCompleted = false, completionPercentage = 70),
            TrainingProgressEntity("PROD-005", "Maxvit 7G", "Nutraceutical", dossierRead = true, videoWatched = true, quizScore = 85, isCompleted = true, completionPercentage = 92),
            TrainingProgressEntity("PROD-006", "Ferosis", "Hematology", dossierRead = true, videoWatched = false, quizScore = 60, isCompleted = false, completionPercentage = 64),
            TrainingProgressEntity("PROD-007", "Nervicobal", "Neurology", dossierRead = true, videoWatched = true, quizScore = 90, isCompleted = true, completionPercentage = 95),
            TrainingProgressEntity("PROD-008", "Cefosis", "Antibiotic", dossierRead = true, videoWatched = false, quizScore = 80, isCompleted = false, completionPercentage = 72),
            TrainingProgressEntity("PROD-009", "Cefodosis", "Antibiotic", dossierRead = false, videoWatched = false, quizScore = 0, isCompleted = false, completionPercentage = 0),
            TrainingProgressEntity("PROD-010", "Amosis", "Antibiotic", dossierRead = true, videoWatched = true, quizScore = 80, isCompleted = true, completionPercentage = 90),
            TrainingProgressEntity("PROD-011", "Lizosis", "Antibiotic", dossierRead = false, videoWatched = false, quizScore = 0, isCompleted = false, completionPercentage = 0),
            TrainingProgressEntity("PROD-012", "Farocare", "Antibiotic", dossierRead = true, videoWatched = true, quizScore = 95, isCompleted = true, completionPercentage = 98),
            TrainingProgressEntity("PROD-013", "Levosis LC", "Respiratory", dossierRead = true, videoWatched = false, quizScore = 70, isCompleted = false, completionPercentage = 68),
            TrainingProgressEntity("PROD-014", "Pandocare DSR", "Gastroenterology", dossierRead = true, videoWatched = true, quizScore = 85, isCompleted = true, completionPercentage = 94),
            TrainingProgressEntity("PROD-015", "Etrosis", "Orthopedics", dossierRead = false, videoWatched = false, quizScore = 0, isCompleted = false, completionPercentage = 0),
            TrainingProgressEntity("PROD-016", "AP Care SP", "Pain Management", dossierRead = true, videoWatched = false, quizScore = 80, isCompleted = false, completionPercentage = 72),
            TrainingProgressEntity("PROD-017", "Drotacare", "Gastroenterology", dossierRead = false, videoWatched = false, quizScore = 0, isCompleted = false, completionPercentage = 0),
            TrainingProgressEntity("PROD-018", "Udiosi", "Hepatology", dossierRead = true, videoWatched = true, quizScore = 90, isCompleted = true, completionPercentage = 95),
            TrainingProgressEntity("PROD-019", "Pandocare Inj", "Critical Care", dossierRead = true, videoWatched = false, quizScore = 60, isCompleted = false, completionPercentage = 64)
        )
    }

    fun getInitialQuestions(): List<AssessmentQuestionEntity> {
        return listOf(
            // Booster Questions
            AssessmentQuestionEntity(
                id = "Q-BOOST-1",
                productId = "PROD-001",
                questionText = "What is the primary cellular function of L-Carnitine in Booster?",
                optionA = "Directly breaks down glycogen in hepatocytes",
                optionB = "Transports long-chain fatty acids into mitochondria for beta-oxidation",
                optionC = "Inhibits serotonin uptake in synapses",
                optionD = "Stimulates osteoblast differentiation",
                correctOptionIndex = 1,
                explanation = "L-Carnitine serves as an essential co-factor shuttling long-chain fatty acids across the inner mitochondrial membrane for beta-oxidation."
            ),
            AssessmentQuestionEntity(
                id = "Q-BOOST-2",
                productId = "PROD-001",
                questionText = "Why is the effervescent delivery form in Booster superior to oily softgels?",
                optionA = "It guarantees 3x faster absorption without oily gastric reflux",
                optionB = "It contains high amount of sucrose",
                optionC = "It alters stomach pH permanently",
                optionD = "It prevents the breakdown of CoQ10 in the colon",
                correctOptionIndex = 0,
                explanation = "Effervescent solution provides 100% dissolved bioavailable CoQ10 with zero greasy regurgitation within 15 minutes."
            ),
            // Calci Fizz Questions
            AssessmentQuestionEntity(
                id = "Q-CALCI-1",
                productId = "PROD-002",
                questionText = "What is the critical role of Vitamin K2-7 in Calci Fizz?",
                optionA = "Increases iron absorption in duodenum",
                optionB = "Activates osteocalcin to direct calcium into bones rather than arteries",
                optionC = "Acts as an antacid for stomach ulcers",
                optionD = "Stimulates insulin secretion",
                correctOptionIndex = 1,
                explanation = "Vitamin K2-7 gamma-carboxylates osteocalcin and Matrix Gla Protein (MGP), safely routing calcium into bone matrix and away from blood vessels."
            ),
            // Metabo 3X Questions
            AssessmentQuestionEntity(
                id = "Q-METABO-1",
                productId = "PROD-003",
                questionText = "Which key metabolic enzyme is activated by Berberine in Metabo 3X?",
                optionA = "Cyclooxygenase-2 (COX-2)",
                optionB = "AMP-Activated Protein Kinase (AMPK)",
                optionC = "Angiotensin Converting Enzyme (ACE)",
                optionD = "HMG-CoA Reductase",
                correctOptionIndex = 1,
                explanation = "Berberine is a well-documented master activator of AMPK, enhancing glucose uptake via GLUT-4 translocation."
            ),
            // Farocare Questions
            AssessmentQuestionEntity(
                id = "Q-FARO-1",
                productId = "PROD-004",
                questionText = "What makes Farocare (Faropenem) distinct from other oral antibiotics?",
                optionA = "It is the only orally bioavailable penem antibiotic stable against ESBLs",
                optionB = "It only treats viral upper respiratory infections",
                optionC = "It acts exclusively on fungal cell walls",
                optionD = "It has an irreversible macrolide structure",
                correctOptionIndex = 0,
                explanation = "Faropenem is an oral penem class bactericidal antibiotic with high stability against extended-spectrum beta-lactamases (ESBLs)."
            )
        )
    }

    fun getInitialDoctors(): List<DoctorEntity> {
        return listOf(
            DoctorEntity(
                id = "DOC-101",
                name = "Dr. Rajesh Sharma",
                specialty = "Cardiologist",
                qualification = "MD, DM (Cardiology), FACC",
                clinicHospital = "Sharma Heart & Vascular Clinic, Rohini Sec-9",
                address = "Plot 42, Sector 9, Rohini, New Delhi",
                phone = "+91 98112 34567",
                email = "dr.sharma.cardio@gmail.com",
                preferredVisitingTime = "09:30 AM - 11:30 AM",
                birthday = "14 Sep",
                anniversary = "22 Nov",
                potentialCategory = "A",
                priority = "High",
                notes = "Key opinion leader. Highly interested in Booster for statin myopathy and Metabo 3X for metabolic syndrome.",
                lastVisitDate = "14 Aug 2026",
                nextFollowUpDate = "20 Aug 2026",
                productsDiscussed = "Booster, Metabo 3X, Udiosi"
            ),
            DoctorEntity(
                id = "DOC-102",
                name = "Dr. Ananya Mehta",
                specialty = "Consultant Physician & Diabetologist",
                qualification = "MBBS, MD (Internal Medicine)",
                clinicHospital = "Mehta Diabetes & Wellness Care, Pitampura",
                address = "FD-18, Near Madhuban Chowk, Pitampura, Delhi",
                phone = "+91 98731 22445",
                email = "ananya.mehta@careclinic.in",
                preferredVisitingTime = "11:30 AM - 01:00 PM",
                birthday = "05 Mar",
                anniversary = "18 Dec",
                potentialCategory = "A",
                priority = "High",
                notes = "High-prescriber for Metabo 3X and Nervicobal. Requested 5 sample strips of Booster.",
                lastVisitDate = "12 Aug 2026",
                nextFollowUpDate = "21 Aug 2026",
                productsDiscussed = "Metabo 3X, Nervicobal, Booster"
            ),
            DoctorEntity(
                id = "DOC-103",
                name = "Dr. Vikram Sethi",
                specialty = "Senior Orthopedic Surgeon",
                qualification = "MS (Ortho), MCh (Joint Replacement)",
                clinicHospital = "Sethi Bone & Joint Centre, Model Town",
                address = "B-4/12, Model Town Phase 2, Delhi",
                phone = "+91 98100 88765",
                email = "drvikramsethi@orthocare.org",
                preferredVisitingTime = "02:30 PM - 04:00 PM",
                birthday = "28 Jul",
                anniversary = "10 May",
                potentialCategory = "A",
                priority = "High",
                notes = "Prescribes Calci Fizz and Etrosis heavily. Emphasized K2-7 superiority over plain calcium.",
                lastVisitDate = "15 Aug 2026",
                nextFollowUpDate = "22 Aug 2026",
                productsDiscussed = "Calci Fizz, Etrosis, AP Care SP"
            ),
            DoctorEntity(
                id = "DOC-104",
                name = "Dr. Sunita Kapoor",
                specialty = "Gastroenterologist & Hepatologist",
                qualification = "MD, DNB (Gastroenterology)",
                clinicHospital = "Apex Gastro Institute, Shalimar Bagh",
                address = "Pocket AB, Shalimar Bagh, Delhi",
                phone = "+91 98224 11990",
                email = "sunita.gastro@apexhealth.in",
                preferredVisitingTime = "05:00 PM - 07:00 PM",
                birthday = "19 Nov",
                anniversary = "04 Feb",
                potentialCategory = "B",
                priority = "Medium",
                notes = "Prescribes Pandocare DSR, Nutri Digest, and Udiosi for fatty liver cohorts.",
                lastVisitDate = "10 Aug 2026",
                nextFollowUpDate = "24 Aug 2026",
                productsDiscussed = "Nutri Digest, Udiosi, Pandocare DSR"
            ),
            DoctorEntity(
                id = "DOC-105",
                name = "Dr. Meenakshi Iyer",
                specialty = "Obstetrician & Gynecologist",
                qualification = "MBBS, MS (OBG), FICOG",
                clinicHospital = "Matritva Women's Hospital, Rohini Sec-14",
                address = "Plot 8, Sector 14, Rohini, Delhi",
                phone = "+91 98199 44332",
                email = "meenakshi.iyer@matritva.com",
                preferredVisitingTime = "10:00 AM - 12:00 PM",
                birthday = "02 Oct",
                anniversary = "15 Jun",
                potentialCategory = "A",
                priority = "High",
                notes = "Major supporter of Ferosis for pregnancy anemia and Drotacare for dysmenorrhea.",
                lastVisitDate = "16 Aug 2026",
                nextFollowUpDate = "23 Aug 2026",
                productsDiscussed = "Ferosis, Drotacare, Calci Fizz"
            )
        )
    }

    fun getInitialStockists(): List<StockistEntity> {
        return listOf(
            StockistEntity(
                id = "STK-501",
                companyName = "Apex Pharma Distributors Pvt Ltd",
                contactPerson = "Suresh Agarwal",
                phone = "+91 98111 22334",
                address = "Shop 14-16, Bhagirath Palace, Chandni Chowk, Delhi",
                territory = "North Delhi Wholesale",
                gstNumber = "07AAACA1234F1Z8",
                creditLimit = 1500000.0,
                outstandingAmount = 345000.0,
                lastOrderDate = "16 Aug 2026",
                totalSales = 2450000.0,
                status = "Active"
            ),
            StockistEntity(
                id = "STK-502",
                companyName = "Medlink Healthcare Logistics",
                contactPerson = "Harish Bhatia",
                phone = "+91 98222 44556",
                address = "Warehouse 4, Transport Nagar, GT Karnal Road, Delhi",
                territory = "Rohini & Outer Delhi",
                gstNumber = "07BBCDB5678G2Z1",
                creditLimit = 1000000.0,
                outstandingAmount = 180000.0,
                lastOrderDate = "14 Aug 2026",
                totalSales = 1820000.0,
                status = "Active"
            )
        )
    }

    fun getInitialRetailers(): List<RetailerEntity> {
        return listOf(
            RetailerEntity(
                id = "RET-701",
                shopName = "Sanjivani Medical & General Store",
                ownerName = "Pramod Gupta",
                phone = "+91 98333 77889",
                address = "Near Metro Pillar 382, Rohini Sec-9, Delhi",
                stockistName = "Apex Pharma Distributors",
                territory = "Rohini Central",
                productsStocked = "Booster, Calci Fizz, Metabo 3X, Pandocare DSR",
                lastOrderDate = "15 Aug 2026",
                outstandingAmount = 14500.0,
                notes = "Serves Dr. Sharma's patients. High demand for Booster 10s pack."
            ),
            RetailerEntity(
                id = "RET-702",
                shopName = "Apollo Pharmacy Franchise #419",
                ownerName = "Naveen Juneja (Store Manager)",
                phone = "+91 98444 66112",
                address = "Main Market, Block C, Pitampura, Delhi",
                stockistName = "Medlink Healthcare",
                territory = "Pitampura",
                productsStocked = "Maxvit 7G, Calci Fizz, Nervicobal, Cefosis",
                lastOrderDate = "17 Aug 2026",
                outstandingAmount = 28000.0,
                notes = "High footfall corporate pharmacy. Maintains healthy inventory."
            ),
            RetailerEntity(
                id = "RET-703",
                shopName = "Aggarwal Medicos",
                ownerName = "Deepak Aggarwal",
                phone = "+91 98555 11223",
                address = "Opposite Matritva Hospital, Rohini Sec-14, Delhi",
                stockistName = "Apex Pharma Distributors",
                territory = "Rohini Sector 14",
                productsStocked = "Ferosis, Drotacare, Calci Fizz, Pandocare DSR",
                lastOrderDate = "13 Aug 2026",
                outstandingAmount = 8500.0,
                notes = "Primary counter for Dr. Meenakshi Iyer prescriptions."
            )
        )
    }

    fun getInitialRoutes(): List<RoutePlanEntity> {
        return listOf(
            RoutePlanEntity(
                id = "ROUTE-TODAY",
                date = "2026-08-19",
                title = "Rohini Sec-9 to Pitampura Core Circuit",
                doctorCount = 3,
                retailerCount = 2,
                stockistCount = 1,
                stopsListText = "1. Dr. Rajesh Sharma (09:30 AM) • 2. Sanjivani Medicos (10:45 AM) • 3. Dr. Ananya Mehta (11:30 AM) • 4. Apollo Pharmacy (01:15 PM) • 5. Dr. Vikram Sethi (02:45 PM) • 6. Medlink Logistics (04:30 PM)",
                status = "In-Progress"
            )
        )
    }

    fun getInitialFollowUps(): List<FollowUpEntity> {
        return listOf(
            FollowUpEntity(
                id = "FOL-01",
                personName = "Dr. Rajesh Sharma",
                personType = "Doctor",
                relatedId = "DOC-101",
                followUpDate = "Today, 04:30 PM",
                reason = "Deliver 5 sample packs of Booster for Post-COVID fatigue patient trial",
                priority = "High",
                notes = "Doctor requested samples during morning briefing",
                status = "Pending"
            ),
            FollowUpEntity(
                id = "FOL-02",
                personName = "Sanjivani Medical Store",
                personType = "Retailer",
                relatedId = "RET-701",
                followUpDate = "Today, 05:15 PM",
                reason = "Collect payment cheque of ₹14,500 for August batch invoice",
                priority = "Medium",
                notes = "Owner asked to visit after 5 PM",
                status = "Pending"
            ),
            FollowUpEntity(
                id = "FOL-03",
                personName = "Dr. Ananya Mehta",
                personType = "Doctor",
                relatedId = "DOC-102",
                followUpDate = "Tomorrow, 11:30 AM",
                reason = "Discuss clinical efficacy trial results of Metabo 3X in PCOS patients",
                priority = "High",
                notes = "Bring the Metabo 3X clinical study paper",
                status = "Pending"
            )
        )
    }

    fun getInitialNotifications(): List<NotificationEntity> {
        return listOf(
            NotificationEntity(
                id = "NOTIF-01",
                title = "Doctor Visit Reminder",
                message = "Upcoming visit with Dr. Rajesh Sharma at 09:30 AM at Rohini Sec-9.",
                type = "Visit",
                timeFormatted = "15 mins ago",
                isRead = false,
                actionRoute = "visits"
            ),
            NotificationEntity(
                id = "NOTIF-02",
                title = "Order Approved",
                message = "Order #ORD-8491 (₹18,500) for Sanjivani Medicos approved by Apex Pharma.",
                type = "Order",
                timeFormatted = "2 hours ago",
                isRead = false,
                actionRoute = "orders"
            ),
            NotificationEntity(
                id = "NOTIF-03",
                title = "Incentive Milestone Reached!",
                message = "Congratulations! You crossed 75% monthly sales target. Estimated incentive: ₹8,450.",
                type = "Incentive",
                timeFormatted = "Yesterday",
                isRead = true,
                actionRoute = "incentives"
            ),
            NotificationEntity(
                id = "NOTIF-04",
                title = "New Academy Masterclass",
                message = "Farocare ER penem antibiotic masterclass and quiz are now live in Academy.",
                type = "Training",
                timeFormatted = "2 days ago",
                isRead = true,
                actionRoute = "academy"
            )
        )
    }

    fun getInitialAchievements(): List<AchievementEntity> {
        return listOf(
            AchievementEntity(
                id = "ACH-01",
                title = "First 10 Visits",
                description = "Successfully complete first 10 recorded doctor calls in the territory",
                iconCategory = "Visits",
                progress = 10,
                maxProgress = 10,
                isUnlocked = true,
                unlockedDate = "05 Feb 2026"
            ),
            AchievementEntity(
                id = "ACH-02",
                title = "50 Doctor Visits",
                description = "Conduct 50 comprehensive doctor visits with sample reports",
                iconCategory = "Visits",
                progress = 50,
                maxProgress = 50,
                isUnlocked = true,
                unlockedDate = "15 Mar 2026"
            ),
            AchievementEntity(
                id = "ACH-03",
                title = "100 Doctor Visits Milestone",
                description = "Achieve 100 successful doctor field calls with high customer satisfaction",
                iconCategory = "Visits",
                progress = 84,
                maxProgress = 100,
                isUnlocked = false
            ),
            AchievementEntity(
                id = "ACH-04",
                title = "₹1 Lakh Monthly Sales",
                description = "Generate orders exceeding ₹1,00,000 in a single calendar month",
                iconCategory = "Sales",
                progress = 100000,
                maxProgress = 100000,
                isUnlocked = true,
                unlockedDate = "31 May 2026"
            ),
            AchievementEntity(
                id = "ACH-05",
                title = "Booster Champion",
                description = "Score 90%+ in Booster Academy exam and achieve 500 units booked",
                iconCategory = "Academy",
                progress = 95,
                maxProgress = 100,
                isUnlocked = true,
                unlockedDate = "10 Jul 2026"
            ),
            AchievementEntity(
                id = "ACH-06",
                title = "CareOsis Academy Master",
                description = "Complete training dossiers and quizzes for all 19 pharmaceutical products",
                iconCategory = "Academy",
                progress = 15,
                maxProgress = 19,
                isUnlocked = false
            )
        )
    }

    fun getInitialLeaderboard(): List<LeaderboardEntity> {
        return listOf(
            LeaderboardEntity(id = "LB-1", rank = 1, mrName = "Rohan Khurana", territory = "South Delhi & Saket", sales = 215000.0, achievementPercent = 107.5, visitsCount = 142, trainingPercent = 95, points = 2850),
            LeaderboardEntity(id = "LB-2", rank = 2, mrName = "Aman Chhabra (You)", territory = "North Delhi & Rohini", sales = 156800.0, achievementPercent = 78.4, visitsCount = 128, trainingPercent = 78, points = 2140),
            LeaderboardEntity(id = "LB-3", rank = 3, mrName = "Pooja Deshmukh", territory = "East Delhi & Laxmi Nagar", sales = 148000.0, achievementPercent = 74.0, visitsCount = 119, trainingPercent = 88, points = 1980),
            LeaderboardEntity(id = "LB-4", rank = 4, mrName = "Siddharth Joshi", territory = "West Delhi & Janakpuri", sales = 135000.0, achievementPercent = 67.5, visitsCount = 105, trainingPercent = 70, points = 1750),
            LeaderboardEntity(id = "LB-5", rank = 5, mrName = "Kunal Bhatnagar", territory = "Central Delhi & Connaught", sales = 122000.0, achievementPercent = 61.0, visitsCount = 98, trainingPercent = 65, points = 1520)
        )
    }

    fun getInitialOrders(): List<OrderEntity> {
        return listOf(
            OrderEntity(
                id = "ORD-8491",
                customerId = "RET-701",
                customerName = "Sanjivani Medical Store",
                customerType = "RETAILER",
                mrId = "CO-MR-8492",
                orderDate = "19 Aug 2026",
                subtotal = 18500.0,
                discountPercent = 5.0,
                discountAmount = 925.0,
                gstAmount = 2109.0,
                totalAmount = 19684.0,
                itemsSummary = "Booster x 20, Calci Fizz x 15, Pandocare DSR x 30",
                status = "Submitted",
                notes = "Priority delivery requested before 4 PM",
                isSynced = true
            ),
            OrderEntity(
                id = "ORD-8488",
                customerId = "STK-501",
                customerName = "Apex Pharma Distributors",
                customerType = "STOCKIST",
                mrId = "CO-MR-8492",
                orderDate = "16 Aug 2026",
                subtotal = 84000.0,
                discountPercent = 8.0,
                discountAmount = 6720.0,
                gstAmount = 9273.6,
                totalAmount = 86553.6,
                itemsSummary = "Metabo 3X x 100, Booster x 150, Nervicobal x 80",
                status = "Dispatched",
                notes = "Weekly stock replenishment order",
                isSynced = true
            )
        )
    }

    fun getInitialExpenses(): List<ExpenseEntity> {
        return listOf(
            ExpenseEntity(
                id = "EXP-301",
                date = "19 Aug 2026",
                category = "Fuel",
                amount = 450.0,
                description = "Field travel fuel (Rohini Sector 9 to Pitampura circuit - 128 km)",
                location = "Rohini IOCL Station",
                receiptPath = "receipt_fuel_19aug.jpg",
                status = "Submitted",
                isSynced = true
            ),
            ExpenseEntity(
                id = "EXP-302",
                date = "19 Aug 2026",
                category = "Food",
                amount = 180.0,
                description = "Afternoon field refreshment & lunch allowance",
                location = "Pitampura Market",
                receiptPath = "receipt_food_19aug.jpg",
                status = "Submitted",
                isSynced = true
            ),
            ExpenseEntity(
                id = "EXP-298",
                date = "18 Aug 2026",
                category = "Travel",
                amount = 350.0,
                description = "Metro feeder & intra-city travel allowance for North Delhi route",
                location = "Model Town & GTB Nagar",
                receiptPath = "",
                status = "Approved",
                isSynced = true
            ),
            ExpenseEntity(
                id = "EXP-297",
                date = "18 Aug 2026",
                category = "Parking",
                amount = 120.0,
                description = "Hospital multi-level parking slips (Max Hospital & Apex Gastro)",
                location = "Shalimar Bagh",
                receiptPath = "receipt_parking_18aug.jpg",
                status = "Approved",
                isSynced = true
            ),
            ExpenseEntity(
                id = "EXP-295",
                date = "17 Aug 2026",
                category = "Doctor Engagement",
                amount = 650.0,
                description = "Clinical journal discussion tea & refreshments with Dr. Sharma team",
                location = "Rohini Sector 9",
                receiptPath = "receipt_doc_refresh_17aug.jpg",
                status = "Approved",
                isSynced = true
            ),
            ExpenseEntity(
                id = "EXP-292",
                date = "16 Aug 2026",
                category = "Fuel",
                amount = 520.0,
                description = "Inter-territory travel to Stockist Depot & Retailer hubs",
                location = "Azadpur BPCL Station",
                receiptPath = "receipt_fuel_16aug.jpg",
                status = "Approved",
                isSynced = true
            ),
            ExpenseEntity(
                id = "EXP-288",
                date = "14 Aug 2026",
                category = "Hotel",
                amount = 1800.0,
                description = "Overnight stay for Regional Cycle Meeting at North Zone HQ",
                location = "Zone Transit Hotel, Civil Lines",
                receiptPath = "receipt_hotel_14aug.jpg",
                status = "Approved",
                isSynced = true
            ),
            ExpenseEntity(
                id = "EXP-285",
                date = "12 Aug 2026",
                category = "Other",
                amount = 220.0,
                description = "Urgent color printout of Booster clinical monographs for Dr. Mehta",
                location = "Pitampura Print Hub",
                receiptPath = "receipt_print_12aug.jpg",
                status = "Approved",
                isSynced = true
            )
        )
    }


    fun getInitialVisits(): List<DoctorVisitEntity> {
        return listOf(
            DoctorVisitEntity(
                id = "VISIT-901",
                doctorId = "DOC-101",
                doctorName = "Dr. Rajesh Sharma",
                clinicName = "Sharma Heart Clinic",
                startTime = "09:30 AM",
                endTime = "09:55 AM",
                visitDate = "19 Aug 2026",
                purpose = "Product Reminder & Sample Follow-up",
                doctorResponse = "Positive",
                prescriptionPotential = "High",
                samplesGiven = "Booster (2 packs), Metabo 3X (1 pack)",
                productsDiscussed = "Booster, Metabo 3X",
                nextFollowUpDate = "26 Aug 2026",
                notes = "Doctor agreed to initiate Booster for 10 post-cardiac surgery recovery patients with chronic fatigue.",
                status = "Completed",
                isSynced = true
            ),
            DoctorVisitEntity(
                id = "VISIT-902",
                doctorId = "DOC-102",
                doctorName = "Dr. Ananya Mehta",
                clinicName = "Mehta Diabetes Care",
                startTime = "11:30 AM",
                endTime = "11:50 AM",
                visitDate = "19 Aug 2026",
                purpose = "New Product Introduction",
                doctorResponse = "Interested",
                prescriptionPotential = "High",
                samplesGiven = "Metabo 3X (2 packs), Nervicobal (1 pack)",
                productsDiscussed = "Metabo 3X, Nervicobal",
                nextFollowUpDate = "21 Aug 2026",
                notes = "Doctor reviewed the AMPK mechanism of Metabo 3X with great interest. Discussed PCOS glycemic control.",
                status = "Completed",
                isSynced = true
            )
        )
    }

    fun getInitialUserAccounts(): List<UserAccountEntity> {
        return listOf(
            UserAccountEntity(
                id = "CO-SA-001",
                name = "CareOsis Executive Super Admin",
                email = "executive.hq@careosis.com",
                phone = "+91 98100 00001",
                role = "SUPER_ADMIN",
                password = "CareOsisSuper@2026",
                status = "ACTIVE",
                assignedRegionIds = "GLOBAL,DELHI_NCR,NOIDA,GHAZIABAD,GURGAON,MUMBAI_CENTRAL,BENGALURU_SOUTH",
                employeeScopeMode = "ALL_IN_REGION",
                assignedEmployeeIds = "ALL",
                permissions = "ALL_PERMISSIONS,GLOBAL_CONTROL,MANAGE_ADMINS,MANAGE_REGIONS,EDIT_INCENTIVE_RULES,EDIT_SALARY,VIEW_SALARY,APPROVE_ORDER,APPROVE_EXPENSE",
                canCreateEmployees = true,
                designation = "Chief Executive Operating Authority",
                territoryName = "CareOsis Global Supply HQ",
                joiningDate = "01 Jan 2024"
            ),
            UserAccountEntity(
                id = "CO-ADM-101",
                name = "Rajesh Verma",
                email = "rajesh.verma@careosis.com",
                phone = "+91 98111 22334",
                role = "ADMIN",
                password = "AdminPass@2026",
                status = "ACTIVE",
                assignedRegionIds = "DELHI_NCR,NOIDA",
                employeeScopeMode = "ALL_IN_REGION",
                assignedEmployeeIds = "CO-MR-8492,CO-MR-8494,CO-MR-8495",
                permissions = "VIEW_EMPLOYEES,CREATE_EMPLOYEE,EDIT_EMPLOYEE,VIEW_DOCTORS,CREATE_DOCTOR,VIEW_ORDERS,APPROVE_ORDER,VIEW_EXPENSES,APPROVE_EXPENSE,VIEW_REPORTS,VIEW_INCENTIVES",
                canCreateEmployees = true,
                designation = "Regional Operations Director (North Zone)",
                territoryName = "Delhi NCR & Noida Zone",
                joiningDate = "01 Feb 2024"
            ),
            UserAccountEntity(
                id = "CO-ADM-102",
                name = "Vikram Malhotra",
                email = "vikram.malhotra@careosis.com",
                phone = "+91 98222 33445",
                role = "ADMIN",
                password = "AdminPass@2026",
                status = "ACTIVE",
                assignedRegionIds = "GURGAON,GHAZIABAD",
                employeeScopeMode = "SPECIFIC_EMPLOYEES",
                assignedEmployeeIds = "CO-MR-8493",
                permissions = "VIEW_EMPLOYEES,VIEW_DOCTORS,VIEW_ORDERS,APPROVE_ORDER,VIEW_EXPENSES,APPROVE_EXPENSE,VIEW_REPORTS",
                canCreateEmployees = false,
                designation = "Zonal Operations Manager (Haryana & East)",
                territoryName = "Gurgaon & Ghaziabad Territory",
                joiningDate = "15 Mar 2024"
            ),
            UserAccountEntity(
                id = "CO-MR-8492",
                name = "Aman Chhabra",
                email = "aman.chhabra@careosis.com",
                phone = "+91 98765 43210",
                role = "EMPLOYEE",
                password = "CareOsis@2026",
                status = "ACTIVE",
                assignedRegionIds = "DELHI_NCR",
                employeeScopeMode = "ALL_IN_REGION",
                assignedEmployeeIds = "SELF",
                permissions = "FIELD_OPERATIONS,LOG_VISITS,SUBMIT_ORDERS,CLAIM_EXPENSES,VIEW_TRAINING",
                canCreateEmployees = false,
                baseSalary = 38000.0,
                fixedAllowance = 8500.0,
                travelAllowance = 6000.0,
                otherAllowance = 2500.0,
                deductions = 1800.0,
                monthlyTarget = 200000.0,
                reportingAdminId = "CO-ADM-101",
                designation = "Senior Medical Representative",
                territoryName = "North Delhi & Rohini Central",
                joiningDate = "15 Jan 2025"
            ),
            UserAccountEntity(
                id = "CO-MR-8493",
                name = "Rohan Mehra",
                email = "rohan.mehra@careosis.com",
                phone = "+91 98765 11223",
                role = "EMPLOYEE",
                password = "CareOsis@2026",
                status = "ACTIVE",
                assignedRegionIds = "GURGAON",
                employeeScopeMode = "ALL_IN_REGION",
                assignedEmployeeIds = "SELF",
                permissions = "FIELD_OPERATIONS,LOG_VISITS,SUBMIT_ORDERS,CLAIM_EXPENSES,VIEW_TRAINING",
                canCreateEmployees = false,
                baseSalary = 32000.0,
                fixedAllowance = 7500.0,
                travelAllowance = 5000.0,
                otherAllowance = 2000.0,
                deductions = 1500.0,
                monthlyTarget = 180000.0,
                reportingAdminId = "CO-ADM-102",
                designation = "Medical Representative",
                territoryName = "Gurgaon Cyber City Hub",
                joiningDate = "10 Feb 2025"
            ),
            UserAccountEntity(
                id = "CO-MR-8494",
                name = "Priya Sharma",
                email = "priya.sharma@careosis.com",
                phone = "+91 98765 33445",
                role = "EMPLOYEE",
                password = "CareOsis@2026",
                status = "ACTIVE",
                assignedRegionIds = "NOIDA",
                employeeScopeMode = "ALL_IN_REGION",
                assignedEmployeeIds = "SELF",
                permissions = "FIELD_OPERATIONS,LOG_VISITS,SUBMIT_ORDERS,CLAIM_EXPENSES,VIEW_TRAINING",
                canCreateEmployees = false,
                baseSalary = 34000.0,
                fixedAllowance = 8000.0,
                travelAllowance = 5500.0,
                otherAllowance = 2200.0,
                deductions = 1600.0,
                monthlyTarget = 190000.0,
                reportingAdminId = "CO-ADM-101",
                designation = "Medical Representative",
                territoryName = "Noida Expressway & Greater Noida",
                joiningDate = "01 Mar 2025"
            ),
            UserAccountEntity(
                id = "CO-MR-8495",
                name = "Amitabh Sen",
                email = "amitabh.sen@careosis.com",
                phone = "+91 98765 55667",
                role = "EMPLOYEE",
                password = "CareOsis@2026",
                status = "ACTIVE",
                assignedRegionIds = "DELHI_NCR",
                employeeScopeMode = "ALL_IN_REGION",
                assignedEmployeeIds = "SELF",
                permissions = "FIELD_OPERATIONS,LOG_VISITS,SUBMIT_ORDERS,CLAIM_EXPENSES,VIEW_TRAINING",
                canCreateEmployees = false,
                baseSalary = 30000.0,
                fixedAllowance = 7000.0,
                travelAllowance = 4500.0,
                otherAllowance = 1800.0,
                deductions = 1400.0,
                monthlyTarget = 160000.0,
                reportingAdminId = "CO-ADM-101",
                designation = "Associate MR",
                territoryName = "South Delhi & Connaught Place",
                joiningDate = "15 Apr 2025"
            )
        )
    }

    fun getInitialRegions(): List<RegionEntity> {
        return listOf(
            RegionEntity(
                id = "DELHI_NCR",
                name = "Delhi National Capital Region",
                state = "Delhi",
                code = "DL-NCR",
                headquarters = "Connaught Place, New Delhi",
                activeMRCount = 14,
                doctorCount = 168,
                monthlyTarget = 2800000.0
            ),
            RegionEntity(
                id = "NOIDA",
                name = "Noida & Greater Noida Zone",
                state = "Uttar Pradesh",
                code = "UP-NOI",
                headquarters = "Sector 62, Noida",
                activeMRCount = 8,
                doctorCount = 94,
                monthlyTarget = 1500000.0
            ),
            RegionEntity(
                id = "GHAZIABAD",
                name = "Ghaziabad & East UP Border",
                state = "Uttar Pradesh",
                code = "UP-GZB",
                headquarters = "RDC Raj Nagar, Ghaziabad",
                activeMRCount = 6,
                doctorCount = 76,
                monthlyTarget = 1200000.0
            ),
            RegionEntity(
                id = "GURGAON",
                name = "Gurgaon Cyber Hub & Haryana",
                state = "Haryana",
                code = "HR-GGN",
                headquarters = "Cyber City DLF Phase 2, Gurgaon",
                activeMRCount = 10,
                doctorCount = 120,
                monthlyTarget = 2000000.0
            ),
            RegionEntity(
                id = "MUMBAI_CENTRAL",
                name = "Mumbai Central & Thane Zone",
                state = "Maharashtra",
                code = "MH-MUM",
                headquarters = "Bandra Kurla Complex, Mumbai",
                activeMRCount = 16,
                doctorCount = 210,
                monthlyTarget = 3600000.0
            ),
            RegionEntity(
                id = "BENGALURU_SOUTH",
                name = "Bengaluru South Corridor",
                state = "Karnataka",
                code = "KA-BLR",
                headquarters = "Koramangala, Bengaluru",
                activeMRCount = 12,
                doctorCount = 145,
                monthlyTarget = 2400000.0
            )
        )
    }

    fun getInitialIncentiveRules(): List<IncentiveRuleEntity> {
        val standardPercentageSlabs = "[{\"id\":\"s1\",\"min\":0.0,\"max\":50.0,\"rate\":0.0,\"fixed\":0.0,\"label\":\"Below 50%\"},{\"id\":\"s2\",\"min\":50.0,\"max\":70.0,\"rate\":1.0,\"fixed\":0.0,\"label\":\"50%–69.99%\"},{\"id\":\"s3\",\"min\":70.0,\"max\":90.0,\"rate\":2.0,\"fixed\":0.0,\"label\":\"70%–89.99%\"},{\"id\":\"s4\",\"min\":90.0,\"max\":100.0,\"rate\":3.0,\"fixed\":0.0,\"label\":\"90%–99.99%\"},{\"id\":\"s5\",\"min\":100.0,\"max\":500.0,\"rate\":5.0,\"fixed\":0.0,\"label\":\"100%+\"}]"
        val standardFixedSlabs = "[{\"id\":\"f1\",\"min\":0.0,\"max\":50.0,\"rate\":0.0,\"fixed\":0.0,\"label\":\"Below 50%\"},{\"id\":\"f2\",\"min\":50.0,\"max\":70.0,\"rate\":0.0,\"fixed\":1000.0,\"label\":\"50%–69.99%\"},{\"id\":\"f3\",\"min\":70.0,\"max\":80.0,\"rate\":0.0,\"fixed\":2500.0,\"label\":\"70%–79.99%\"},{\"id\":\"f4\",\"min\":80.0,\"max\":90.0,\"rate\":0.0,\"fixed\":4000.0,\"label\":\"80%–89.99%\"},{\"id\":\"f5\",\"min\":90.0,\"max\":100.0,\"rate\":0.0,\"fixed\":6000.0,\"label\":\"90%–99.99%\"},{\"id\":\"f6\",\"min\":100.0,\"max\":110.0,\"rate\":0.0,\"fixed\":8000.0,\"label\":\"100%–109.99%\"},{\"id\":\"f7\",\"min\":110.0,\"max\":500.0,\"rate\":0.0,\"fixed\":12000.0,\"label\":\"110%+\"}]"
        val multiComponentConfig = "{\"salesThresholdPercent\":80.0,\"salesIncentivePercent\":3.0,\"doctorCoverageThresholdPercent\":80.0,\"doctorCoverageReward\":1000.0,\"newDoctorCountThreshold\":5,\"newDoctorReward\":500.0,\"collectionThresholdPercent\":90.0,\"collectionReward\":1000.0}"

        return listOf(
            IncentiveRuleEntity(
                id = "RULE-SALES-2026-V1",
                ruleName = "CareOsis National Sales Slab Engine",
                ruleType = "PERCENTAGE_OF_SALES",
                targetSource = "TOTAL_SALES",
                defaultTarget = 200000.0,
                targetPriority = "EMPLOYEE_FIRST",
                slabsJson = standardPercentageSlabs,
                regionId = "GLOBAL",
                assignedEmployeeIds = "ALL",
                employeeCategory = "ALL",
                priority = 4,
                versionNumber = 1,
                effectiveFrom = "01-08-2026",
                effectiveTo = "31-12-2026",
                status = "ACTIVE",
                formulaDescription = "Actual Sales × Applicable Rate (0% - 5%) based on target achievement tier",
                updatedBy = "CO-ADM-101"
            ),
            IncentiveRuleEntity(
                id = "RULE-DELHI-SLAB-V1",
                ruleName = "Delhi NCR High Growth Fixed Slab Rule",
                ruleType = "SLAB_BASED",
                targetSource = "TOTAL_SALES",
                defaultTarget = 200000.0,
                targetPriority = "EMPLOYEE_FIRST",
                slabsJson = standardFixedSlabs,
                regionId = "DELHI_NCR",
                assignedEmployeeIds = "ALL",
                employeeCategory = "ALL",
                priority = 2,
                versionNumber = 1,
                effectiveFrom = "01-08-2026",
                effectiveTo = "31-12-2026",
                status = "ACTIVE",
                formulaDescription = "Progressive fixed payouts up to ₹12,000 for Delhi NCR field representatives",
                updatedBy = "CO-ADM-101"
            ),
            IncentiveRuleEntity(
                id = "RULE-MULTI-PERF-V1",
                ruleName = "Multi-Component Comprehensive Incentive Matrix",
                ruleType = "MULTI_COMPONENT",
                targetSource = "TOTAL_SALES",
                defaultTarget = 200000.0,
                targetPriority = "EMPLOYEE_FIRST",
                componentsJson = multiComponentConfig,
                regionId = "GLOBAL",
                assignedEmployeeIds = "ALL",
                employeeCategory = "ALL",
                priority = 3,
                versionNumber = 1,
                effectiveFrom = "01-08-2026",
                effectiveTo = "31-12-2026",
                status = "ACTIVE",
                formulaDescription = "Sales % + Doctor Coverage (₹1,000) + New HCPs (₹500) + Collection (₹1,000)",
                updatedBy = "CO-SA-001"
            )
        )
    }

    fun getInitialIncentiveRecords(): List<IncentiveRecordEntity> {
        val augBreakdown = "[{\"title\":\"Sales Volume Incentive\",\"description\":\"₹1,64,000 × 3.0% (80%–89.99% Slab)\",\"amount\":4920.0,\"rateOrUnit\":\"3%\"},{\"title\":\"Doctor Coverage Milestone\",\"description\":\"85% HCP Coverage Achieved (17/20 Target)\",\"amount\":1000.0,\"rateOrUnit\":\"₹1,000\"},{\"title\":\"New Doctor Activation\",\"description\":\"6 New Prescribers Onboarded (Min: 5)\",\"amount\":500.0,\"rateOrUnit\":\"₹500\"},{\"title\":\"Commercial Collection\",\"description\":\"92% On-time Payment Clearance\",\"amount\":2030.0,\"rateOrUnit\":\"₹2,030\"}]"
        val julBreakdown = "[{\"title\":\"Sales Volume Incentive\",\"description\":\"₹1,84,000 × 3.0% (90%–99.99% Slab)\",\"amount\":5520.0,\"rateOrUnit\":\"3%\"},{\"title\":\"Doctor Coverage Milestone\",\"description\":\"90% HCP Coverage Achieved\",\"amount\":1000.0,\"rateOrUnit\":\"₹1,000\"},{\"title\":\"New Doctor Activation\",\"description\":\"7 New Prescribers Onboarded\",\"amount\":500.0,\"rateOrUnit\":\"₹500\"},{\"title\":\"Commercial Collection\",\"description\":\"95% Payment Recovered\",\"amount\":2180.0,\"rateOrUnit\":\"₹2,180\"}]"
        val junBreakdown = "[{\"title\":\"Sales Volume Incentive\",\"description\":\"₹1,50,000 × 2.0% (70%–89.99% Slab)\",\"amount\":3000.0,\"rateOrUnit\":\"2%\"},{\"title\":\"Doctor Coverage Milestone\",\"description\":\"82% HCP Coverage Achieved\",\"amount\":1000.0,\"rateOrUnit\":\"₹1,000\"},{\"title\":\"Commercial Collection\",\"description\":\"90% Payment Recovered\",\"amount\":2850.0,\"rateOrUnit\":\"₹2,850\"}]"
        val mayBreakdown = "[{\"title\":\"Sales Volume Incentive\",\"description\":\"₹1,42,000 × 2.0% (70%–89.99% Slab)\",\"amount\":2840.0,\"rateOrUnit\":\"2%\"},{\"title\":\"Doctor Coverage Milestone\",\"description\":\"80% HCP Coverage Achieved\",\"amount\":1000.0,\"rateOrUnit\":\"₹1,000\"},{\"title\":\"Commercial Collection\",\"description\":\"88% Payment Recovered\",\"amount\":2410.0,\"rateOrUnit\":\"₹2,410\"}]"

        return listOf(
            IncentiveRecordEntity(
                id = "INC-CO-MR-8492-AUG-2026",
                employeeId = "CO-MR-8492",
                employeeName = "Rohan Mehta",
                period = "August 2026",
                target = 200000.0,
                actualSales = 164000.0,
                achievementPercent = 82.0,
                ruleId = "RULE-DELHI-SLAB-V1",
                ruleName = "CareOsis National Sales Slab Engine",
                ruleVersion = 1,
                ruleType = "PERCENTAGE_OF_SALES",
                applicableSlab = "80%–89.99%",
                incentiveRate = 3.0,
                baseIncentive = 4920.0,
                coverageIncentive = 1000.0,
                newDoctorIncentive = 500.0,
                collectionIncentive = 2030.0,
                additionalIncentives = 3530.0,
                deductions = 0.0,
                finalIncentive = 8450.0,
                status = "ESTIMATED",
                breakdownJson = augBreakdown,
                calculatedAt = System.currentTimeMillis()
            ),
            IncentiveRecordEntity(
                id = "INC-CO-MR-8492-JUL-2026",
                employeeId = "CO-MR-8492",
                employeeName = "Rohan Mehta",
                period = "July 2026",
                target = 200000.0,
                actualSales = 184000.0,
                achievementPercent = 92.0,
                ruleId = "RULE-DELHI-SLAB-V1",
                ruleName = "CareOsis National Sales Slab Engine",
                ruleVersion = 1,
                ruleType = "PERCENTAGE_OF_SALES",
                applicableSlab = "90%–99.99%",
                incentiveRate = 3.0,
                baseIncentive = 5520.0,
                coverageIncentive = 1000.0,
                newDoctorIncentive = 500.0,
                collectionIncentive = 2180.0,
                additionalIncentives = 3680.0,
                deductions = 0.0,
                finalIncentive = 9200.0,
                status = "FINAL",
                breakdownJson = julBreakdown,
                calculatedAt = System.currentTimeMillis() - (30L * 24 * 3600 * 1000),
                approvedAt = System.currentTimeMillis() - (28L * 24 * 3600 * 1000),
                approvedBy = "Rajesh Verma (CO-ADM-101)"
            ),
            IncentiveRecordEntity(
                id = "INC-CO-MR-8492-JUN-2026",
                employeeId = "CO-MR-8492",
                employeeName = "Rohan Mehta",
                period = "June 2026",
                target = 200000.0,
                actualSales = 150000.0,
                achievementPercent = 75.0,
                ruleId = "RULE-DELHI-SLAB-V1",
                ruleName = "CareOsis National Sales Slab Engine",
                ruleVersion = 1,
                ruleType = "PERCENTAGE_OF_SALES",
                applicableSlab = "70%–89.99%",
                incentiveRate = 2.0,
                baseIncentive = 3000.0,
                coverageIncentive = 1000.0,
                newDoctorIncentive = 0.0,
                collectionIncentive = 2850.0,
                additionalIncentives = 3850.0,
                deductions = 0.0,
                finalIncentive = 6850.0,
                status = "FINAL",
                breakdownJson = junBreakdown,
                calculatedAt = System.currentTimeMillis() - (60L * 24 * 3600 * 1000),
                approvedAt = System.currentTimeMillis() - (58L * 24 * 3600 * 1000),
                approvedBy = "Rajesh Verma (CO-ADM-101)"
            ),
            IncentiveRecordEntity(
                id = "INC-CO-MR-8492-MAY-2026",
                employeeId = "CO-MR-8492",
                employeeName = "Rohan Mehta",
                period = "May 2026",
                target = 200000.0,
                actualSales = 142000.0,
                achievementPercent = 71.0,
                ruleId = "RULE-DELHI-SLAB-V1",
                ruleName = "CareOsis National Sales Slab Engine",
                ruleVersion = 1,
                ruleType = "PERCENTAGE_OF_SALES",
                applicableSlab = "70%–89.99%",
                incentiveRate = 2.0,
                baseIncentive = 2840.0,
                coverageIncentive = 1000.0,
                newDoctorIncentive = 0.0,
                collectionIncentive = 2410.0,
                additionalIncentives = 3410.0,
                deductions = 0.0,
                finalIncentive = 6250.0,
                status = "FINAL",
                breakdownJson = mayBreakdown,
                calculatedAt = System.currentTimeMillis() - (90L * 24 * 3600 * 1000),
                approvedAt = System.currentTimeMillis() - (88L * 24 * 3600 * 1000),
                approvedBy = "Rajesh Verma (CO-ADM-101)"
            )
        )
    }

    fun getInitialSalaryRules(): SalaryRuleEntity {
        return SalaryRuleEntity(
            id = "SAL-R1-GLOBAL",
            ruleName = "CareOsis Standard Field Compensation v1",
            baseSalary = 35000.0,
            fixedAllowance = 8000.0,
            travelAllowancePerKm = 4.5,
            dailyAllowancePerDay = 350.0,
            performanceBonusMax = 15000.0,
            deductionPfPercent = 12.0,
            regionId = "GLOBAL",
            versionNumber = 1,
            effectiveFrom = "01-08-2026",
            effectiveTo = "31-12-2026",
            status = "ACTIVE"
        )
    }

    fun getInitialAuditLogs(): List<AuditLogEntity> {
        return listOf(
            AuditLogEntity(
                userId = "CO-SA-001",
                userName = "Executive Super Admin",
                userRole = "SUPER_ADMIN",
                action = "SYSTEM_INITIALIZATION",
                targetEntity = "CareOsis Core Enterprise Cluster",
                oldValue = "",
                newValue = "Deployed Role Security, Region Scopes & Formula Calculation Engine v1",
                formattedDate = "19 Aug 2026, 08:00 AM"
            ),
            AuditLogEntity(
                userId = "CO-SA-001",
                userName = "Executive Super Admin",
                userRole = "SUPER_ADMIN",
                action = "ADMIN_ASSIGNED",
                targetEntity = "Admin Rajesh Verma (CO-ADM-101)",
                oldValue = "Unassigned",
                newValue = "Assigned Regions: Delhi NCR & Noida with Employee Creation Permission",
                formattedDate = "19 Aug 2026, 08:30 AM"
            ),
            AuditLogEntity(
                userId = "CO-SA-001",
                userName = "Executive Super Admin",
                userRole = "SUPER_ADMIN",
                action = "RULE_PUBLISHED",
                targetEntity = "Incentive Rule Engine v1",
                oldValue = "Draft",
                newValue = "Active (Tiers 0% to 5% with Focus Brand Booster)",
                formattedDate = "19 Aug 2026, 09:00 AM"
            )
        )
    }
}
