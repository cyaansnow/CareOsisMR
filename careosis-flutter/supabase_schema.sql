-- ==============================================================================
-- CareOsis MR - Supabase PostgreSQL Schema
-- Run this in your Supabase SQL Editor: https://supabase.com/dashboard/project/smljyhwvdbqipumdgtvy/sql/new
-- ==============================================================================

-- 1. MR Profiles Table
CREATE TABLE IF NOT EXISTS public.mr_profiles (
    id TEXT PRIMARY KEY,
    employee_code TEXT NOT NULL,
    full_name TEXT NOT NULL,
    email TEXT,
    phone TEXT,
    hq_territory TEXT,
    role TEXT DEFAULT 'MR',
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 2. Daily Attendance & Shift Logs
CREATE TABLE IF NOT EXISTS public.attendances (
    id TEXT PRIMARY KEY,
    mr_id TEXT,
    date TEXT NOT NULL,
    check_in_time TEXT,
    check_in_lat DOUBLE PRECISION,
    check_in_lng DOUBLE PRECISION,
    check_in_address TEXT,
    check_out_time TEXT,
    check_out_lat DOUBLE PRECISION,
    check_out_lng DOUBLE PRECISION,
    check_out_address TEXT,
    status TEXT DEFAULT 'PRESENT',
    sync_status TEXT DEFAULT 'SYNCED',
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 3. Doctors Directory
CREATE TABLE IF NOT EXISTS public.doctors (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    specialty TEXT NOT NULL,
    clinic_name TEXT,
    address TEXT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    phone TEXT,
    classification TEXT DEFAULT 'A',
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 4. Field Doctor Visits & Detailing
CREATE TABLE IF NOT EXISTS public.doctor_visits (
    id TEXT PRIMARY KEY,
    mr_id TEXT,
    doctor_id TEXT,
    doctor_name TEXT NOT NULL,
    specialty TEXT,
    clinic_name TEXT,
    visit_timestamp TEXT NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    visit_type TEXT DEFAULT 'PLANNED',
    status TEXT DEFAULT 'COMPLETED',
    remarks TEXT,
    sync_status TEXT DEFAULT 'SYNCED',
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 5. Sales & Field Expenses
CREATE TABLE IF NOT EXISTS public.expenses (
    id TEXT PRIMARY KEY,
    mr_id TEXT,
    expense_date TEXT NOT NULL,
    category TEXT NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    receipt_url TEXT,
    notes TEXT,
    status TEXT DEFAULT 'PENDING',
    sync_status TEXT DEFAULT 'SYNCED',
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 6. Monthly Targets & Incentives
CREATE TABLE IF NOT EXISTS public.targets_incentives (
    id TEXT PRIMARY KEY,
    mr_id TEXT NOT NULL,
    month TEXT NOT NULL,
    target_amount DOUBLE PRECISION NOT NULL DEFAULT 0,
    achieved_amount DOUBLE PRECISION NOT NULL DEFAULT 0,
    incentive_earned DOUBLE PRECISION NOT NULL DEFAULT 0,
    payout_status TEXT DEFAULT 'PENDING',
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Enable Row Level Security (RLS)
ALTER TABLE public.mr_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.attendances ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.doctors ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.doctor_visits ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.expenses ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.targets_incentives ENABLE ROW LEVEL SECURITY;

-- Allow public/anon access for demo & development purposes
DO $$
BEGIN
    -- mr_profiles policies
    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE tablename = 'mr_profiles' AND policyname = 'Public anon access mr_profiles') THEN
        CREATE POLICY "Public anon access mr_profiles" ON public.mr_profiles FOR ALL USING (true) WITH CHECK (true);
    END IF;

    -- attendances policies
    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE tablename = 'attendances' AND policyname = 'Public anon access attendances') THEN
        CREATE POLICY "Public anon access attendances" ON public.attendances FOR ALL USING (true) WITH CHECK (true);
    END IF;

    -- doctors policies
    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE tablename = 'doctors' AND policyname = 'Public anon access doctors') THEN
        CREATE POLICY "Public anon access doctors" ON public.doctors FOR ALL USING (true) WITH CHECK (true);
    END IF;

    -- doctor_visits policies
    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE tablename = 'doctor_visits' AND policyname = 'Public anon access doctor_visits') THEN
        CREATE POLICY "Public anon access doctor_visits" ON public.doctor_visits FOR ALL USING (true) WITH CHECK (true);
    END IF;

    -- expenses policies
    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE tablename = 'expenses' AND policyname = 'Public anon access expenses') THEN
        CREATE POLICY "Public anon access expenses" ON public.expenses FOR ALL USING (true) WITH CHECK (true);
    END IF;

    -- targets_incentives policies
    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE tablename = 'targets_incentives' AND policyname = 'Public anon access targets_incentives') THEN
        CREATE POLICY "Public anon access targets_incentives" ON public.targets_incentives FOR ALL USING (true) WITH CHECK (true);
    END IF;
END
$$;
