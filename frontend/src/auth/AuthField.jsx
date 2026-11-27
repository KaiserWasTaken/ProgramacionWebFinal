import { useState } from 'react'

export default function AuthField({
  autoComplete,
  error,
  id,
  label,
  name,
  onBlur,
  onChange,
  placeholder,
  type = 'text',
  value,
}) {
  const [passwordVisible, setPasswordVisible] = useState(false)
  const isPassword = type === 'password'

  return (
    <div className="form-field">
      <label className="visually-hidden" htmlFor={id}>{label}</label>
      <div className="auth-input-wrapper">
        <input
          autoComplete={autoComplete}
          aria-describedby={error ? `${id}-error` : undefined}
          aria-invalid={Boolean(error)}
          className={`form-input${isPassword ? ' form-input--password' : ''}${error ? ' form-input--invalid' : ''}`}
          id={id}
          name={name}
          onBlur={onBlur}
          onChange={onChange}
          placeholder={placeholder}
          type={isPassword && passwordVisible ? 'text' : type}
          value={value}
        />
        {isPassword && (
          <button
            aria-label={`${passwordVisible ? 'Ocultar' : 'Mostrar'} ${name === 'confirmPassword' ? 'contraseña de confirmación' : label.toLowerCase()}`}
            className="password-toggle"
            onClick={() => setPasswordVisible((visible) => !visible)}
            type="button"
          >
            <svg aria-hidden="true" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.8" viewBox="0 0 24 24">
              <path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6-10-6-10-6Z" />
              <circle cx="12" cy="12" r="3" />
              {passwordVisible && <path d="M4 4l16 16" />}
            </svg>
          </button>
        )}
      </div>
      {error && <p className="field-error" id={`${id}-error`}>{error}</p>}
    </div>
  )
}
