import './App.css'

function App() {
  const resumes = [
    {
      title: "Paradigmas do banco de dados",
      summary: "Meus estudos em relação a banco de dados"
    },
    {
      title: "Como funcionam as linguagens de programação",
      summary: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin dictum placerat quam"
    },
    {
      title: "Rap do Ben 10 (Clássico) O PORTADOR DO OMNITRIX",
      summary: "Meus estudos em relação a banco de dados"
    },
    {
      title: "Por que Thales é um pato?🦆",
      summary: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin dictum placerat quam, vitae blandit ipsum feugiat quis. Sed tellus odio, porta sed ornare ut, vulputate vitae massa. Fusce pharetra sapien tellus, sed interdum lorem cursus ac. Nam sit amet diam elementum, ultrices erat ac, eleifend diam. Etiam ac ornare justo. Curabitur ac lectus vel justo accumsan laoreet. Proin nisl ante, mollis in nulla sed, iaculis luctus justo. Vivamus ut ligula ligula."
    },
    {
      title: "Tipagem DINAMICA X ESTATICA",
      summary: "Meus estudos em relação a banco de dados"
    },
    {
      title: "A volta dos que nao foram",
      summary: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin dictum placerat quam, vitae blandit"
    },
  ]

  return (
    <div>
      <div className='home-container'>
        <div className='home-content'>
          <h1>BASEMENT</h1>
          <p>Repository to publish my studies</p>
        </div>
        <div>
          <img src='https://foolan.vercel.app/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Fimage.98bdeb8d.gif&w=64&q=75' />
        </div>
      </div>
      <div className='resume-container'>
        {resumes.map((resume) => {
          return (
            <div className='resume-content'>
              <h1>{resume.title}</h1>
              <p>{resume.summary}</p>
              <button>Ver mais</button>
            </div>
          )
        })}
      </div>
    </div>
  )
}

export default App
